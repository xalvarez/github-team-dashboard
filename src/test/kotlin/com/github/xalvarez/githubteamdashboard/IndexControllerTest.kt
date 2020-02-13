package com.github.xalvarez.githubteamdashboard

import com.github.xalvarez.githubteamdashboard.github.*
import com.github.xalvarez.githubteamdashboard.github.models.Member
import com.github.xalvarez.githubteamdashboard.github.models.PullRequestModel
import com.github.xalvarez.githubteamdashboard.github.models.ReviewState.*
import com.github.xalvarez.githubteamdashboard.github.models.TeamModel
import io.micronaut.http.HttpStatus.OK
import io.micronaut.test.annotation.MicronautTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations.initMocks
import java.time.LocalDateTime
import java.time.Month.JANUARY
import java.time.format.DateTimeFormatter
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@MicronautTest
internal class IndexControllerTest {

    @InjectMocks
    lateinit var indexController: IndexController

    @Mock
    lateinit var gitHubService: GitHubService

    @BeforeEach
    fun setup() = initMocks(this)

    @Test
    fun `should build model`() {
        givenSuccessfulGitHubServiceResponse()
        val expectedTeamModel = givenExpectedTeamModel()
        val expectedAmountOfPullRequests = 3

        val response = indexController.dashboard()
        val model = response.body() as HashMap<*, *>
        val pullRequests = model["pullRequests"] as List<*>

        assertEquals(response.status(), OK)
        assertEquals(model["team"], expectedTeamModel)
        assertEquals(pullRequests.size, expectedAmountOfPullRequests)
        assertTrue { (pullRequests[0] as PullRequestModel).repositoryName == "example_repo_3" }
        assertTrue { (pullRequests[0] as PullRequestModel).state == PENDING }
        assertTrue { (pullRequests[1] as PullRequestModel).repositoryName == "example_repo_1" }
        assertTrue { (pullRequests[1] as PullRequestModel).state == APPROVED }
        assertTrue { (pullRequests[2] as PullRequestModel).repositoryName == "example_repo_2" }
        assertTrue { (pullRequests[2] as PullRequestModel).state == DECLINED }
        assertNotNull(model["lastUpdate"])
    }

    @Test
    fun `should load homepage`() {
        val response = indexController.index()

        assertEquals(response.status(), OK)
    }

    private fun givenExpectedTeamModel() = TeamModel(
        "example_team",
        listOf(Member("example_team_member_1"))
    )

    private fun givenSuccessfulGitHubServiceResponse() {
        given(gitHubService.fetchDashboardData()).willReturn(buildSuccessfulGitHubDashboardData())
    }

    private fun buildSuccessfulGitHubDashboardData(): GithubDashboardData {
        val author = Author("example_team_member_1")
        val repositories = Repositories(
            listOf(
                Repository("example_repo_1", buildPullRequests(
                    author = author, year = 2010, review = Review(listOf(ReviewNode(APPROVED.name)))
                )),
                Repository("example_repo_2", buildPullRequests(
                    author = author, year = 2012, review = Review(listOf(ReviewNode(APPROVED.name), ReviewNode(DECLINED.name)))
                )),
                Repository("example_repo_3", buildPullRequests(author, 2008))
            )
        )

        val members = Members(listOf(MembersNode("example_team_member_1")))
        val team = Team("example_team", members, repositories)
        val data = Data(Organization(team))
        return GithubDashboardData(data)
    }

    private fun buildHumanReadebleDateTime(datetime: LocalDateTime) =
        datetime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

    private fun buildPullRequests(author: Author, year: Int, review: Review = Review(emptyList())) =
        PullRequests(
            listOf(
                PullRequestNode(
                    "http://example.com/1",
                    createLocalDateTime(year),
                    author,
                    "Add cool feature",
                    review
                )
            )
        )

    private fun createLocalDateTime(year: Int) =
        LocalDateTime.of(year, JANUARY, 1, 1, 1, 1)
}