package com.github.xalvarez.githubteamdashboard

import com.github.xalvarez.githubteamdashboard.github.Author
import com.github.xalvarez.githubteamdashboard.github.Data
import com.github.xalvarez.githubteamdashboard.github.GitHubService
import com.github.xalvarez.githubteamdashboard.github.GithubDashboardData
import com.github.xalvarez.githubteamdashboard.github.Members
import com.github.xalvarez.githubteamdashboard.github.MembersNode
import com.github.xalvarez.githubteamdashboard.github.Organization
import com.github.xalvarez.githubteamdashboard.github.PullRequestNode
import com.github.xalvarez.githubteamdashboard.github.PullRequests
import com.github.xalvarez.githubteamdashboard.github.Repositories
import com.github.xalvarez.githubteamdashboard.github.Repository
import com.github.xalvarez.githubteamdashboard.github.Team
import com.github.xalvarez.githubteamdashboard.github.models.Member
import io.micronaut.test.annotation.MicronautTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations.initMocks
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.test.assertTrue

@MicronautTest
class IndexControllerTest {

    @InjectMocks
    lateinit var indexController: IndexController

    @Mock
    lateinit var gitHubService: GitHubService

    @BeforeEach
    fun setup() {
        initMocks(this)
    }

    @Test
    fun `should build model`() {
        givenSuccessfulGitHubServiceResponse()
        val expectedTeamModel = givenExpectedTeamModel()
        val expectedPullRequestsModel = givenExpectedPullRequestsModel()

        val response = indexController.index()
        val responseBody = response.body() as HashMap<*, *>

        assertTrue((responseBody["team"] as com.github.xalvarez.githubteamdashboard.github.models.Team) == expectedTeamModel)
        assertTrue((responseBody["pullRequests"] as List<*>) == expectedPullRequestsModel)
    }

    private fun givenExpectedTeamModel() = com.github.xalvarez.githubteamdashboard.github.models.Team(
        "example_team",
        listOf(Member("example_team_member_1"))
    )

    private fun givenExpectedPullRequestsModel() = listOf(
        com.github.xalvarez.githubteamdashboard.github.models.PullRequest(
            "http://example.com/1",
            buildHumanReadebleDateTime(LocalDateTime.MIN),
            "example_team_member_1",
            "Add cool feature"
        )
    )

    private fun givenSuccessfulGitHubServiceResponse() {
        given(gitHubService.fetchDashboardData()).willReturn(buildSuccessfulGitHubDashboardData())
    }

    private fun buildSuccessfulGitHubDashboardData(): GithubDashboardData {
        val author = Author("example_team_member_1")
        val pullRequests = PullRequests(listOf(PullRequestNode("http://example.com/1", LocalDateTime.MIN, author, "Add cool feature")))
        val repositories = Repositories(listOf(Repository("example_repo_1", pullRequests)))
        val members = Members(listOf(MembersNode("example_team_member_1")))
        val team = Team("example_team", members, repositories)
        val data = Data(Organization(team))
        return GithubDashboardData(data)
    }

    private fun buildHumanReadebleDateTime(datetime: LocalDateTime) =
        datetime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
}