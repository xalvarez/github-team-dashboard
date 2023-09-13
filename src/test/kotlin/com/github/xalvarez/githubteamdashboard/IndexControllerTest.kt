package com.github.xalvarez.githubteamdashboard

import com.github.xalvarez.githubteamdashboard.github.Author
import com.github.xalvarez.githubteamdashboard.github.Commit
import com.github.xalvarez.githubteamdashboard.github.CommitNode
import com.github.xalvarez.githubteamdashboard.github.CommitStatusCheckState
import com.github.xalvarez.githubteamdashboard.github.Commits
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
import com.github.xalvarez.githubteamdashboard.github.Review
import com.github.xalvarez.githubteamdashboard.github.ReviewNode
import com.github.xalvarez.githubteamdashboard.github.Team
import com.github.xalvarez.githubteamdashboard.github.VulnerabilityAlerts
import com.github.xalvarez.githubteamdashboard.github.models.CheckState
import com.github.xalvarez.githubteamdashboard.github.models.Member
import com.github.xalvarez.githubteamdashboard.github.models.PullRequestModel
import com.github.xalvarez.githubteamdashboard.github.models.ReviewState
import com.github.xalvarez.githubteamdashboard.github.models.SecurityAlert
import com.github.xalvarez.githubteamdashboard.github.models.TeamModel
import io.micronaut.http.HttpStatus.OK
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.ZoneId.systemDefault
import java.time.ZonedDateTime

@MicronautTest
@ExtendWith(MockKExtension::class)
internal class IndexControllerTest {

    @InjectMockKs
    lateinit var indexController: IndexController

    @MockK
    lateinit var gitHubService: GitHubService

    @Test
    fun `should build model`() {
        givenSuccessfulGitHubServiceResponse()
        val expectedTeamModel = givenExpectedTeamModel()
        val expectedAmountOfPullRequests = 7

        val response = indexController.dashboard()
        val model = response.body() as HashMap<*, *>
        val pullRequests = model["pullRequests"] as List<*>
        val securityAlerts = model["securityAlerts"] as List<*>

        assertEquals(response.status(), OK)
        assertEquals(model["team"], expectedTeamModel)
        assertEquals(pullRequests.size, expectedAmountOfPullRequests)
        assertTrue { (pullRequests[0] as PullRequestModel).repositoryName == "example_repo_3" }
        assertTrue { (pullRequests[0] as PullRequestModel).state == ReviewState.PENDING }
        assertTrue { (pullRequests[0] as PullRequestModel).checkState == CheckState.FAILURE }
        assertTrue { (pullRequests[0] as PullRequestModel).repositoryUrl == "example3.com" }
        assertTrue { (pullRequests[1] as PullRequestModel).repositoryName == "example_repo_1" }
        assertTrue { (pullRequests[1] as PullRequestModel).state == ReviewState.APPROVED }
        assertTrue { (pullRequests[1] as PullRequestModel).checkState == CheckState.DRAFT }
        assertTrue { (pullRequests[1] as PullRequestModel).repositoryUrl == "example1.com" }
        assertTrue { (pullRequests[2] as PullRequestModel).repositoryName == "example_repo_2" }
        assertTrue { (pullRequests[2] as PullRequestModel).state == ReviewState.CHANGES_REQUESTED }
        assertTrue { (pullRequests[2] as PullRequestModel).checkState == CheckState.NONE }
        assertTrue { (pullRequests[2] as PullRequestModel).repositoryUrl == "example2.com" }
        assertTrue { (pullRequests[3] as PullRequestModel).repositoryName == "example_repo_4" }
        assertTrue { (pullRequests[3] as PullRequestModel).state == ReviewState.PENDING }
        assertTrue { (pullRequests[3] as PullRequestModel).checkState == CheckState.EXPECTED }
        assertTrue { (pullRequests[3] as PullRequestModel).repositoryUrl == "example4.com" }
        assertTrue { (pullRequests[4] as PullRequestModel).repositoryName == "example_repo_5" }
        assertTrue { (pullRequests[4] as PullRequestModel).state == ReviewState.PENDING }
        assertTrue { (pullRequests[4] as PullRequestModel).checkState == CheckState.PENDING }
        assertTrue { (pullRequests[4] as PullRequestModel).repositoryUrl == "example5.com" }
        assertTrue { (pullRequests[5] as PullRequestModel).repositoryName == "example_repo_6" }
        assertTrue { (pullRequests[5] as PullRequestModel).state == ReviewState.PENDING }
        assertTrue { (pullRequests[5] as PullRequestModel).checkState == CheckState.SUCCESS }
        assertTrue { (pullRequests[5] as PullRequestModel).repositoryUrl == "example6.com" }
        assertTrue { (pullRequests[6] as PullRequestModel).repositoryName == "example_repo_7" }
        assertTrue { (pullRequests[6] as PullRequestModel).state == ReviewState.PENDING }
        assertTrue { (pullRequests[6] as PullRequestModel).checkState == CheckState.NONE }
        assertTrue { (pullRequests[6] as PullRequestModel).repositoryUrl == "example7.com" }
        assertEquals(securityAlerts.size, 1)
        assertEquals((securityAlerts[0] as SecurityAlert).repository, "example_repo_2")
        assertEquals((securityAlerts[0] as SecurityAlert).url, "example2.com/network/alerts")
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
        every { gitHubService.fetchDashboardData() } returns buildSuccessfulGitHubDashboardData()
    }

    private fun buildSuccessfulGitHubDashboardData(): GithubDashboardData {
        val author = Author("example_team_member_1")
        val repositories = Repositories(
            listOf(
                Repository("example_repo_1", buildPullRequests(
                    author = author, year = 2010, review = Review(listOf(
                        ReviewNode(ReviewState.PENDING.name), ReviewNode(ReviewState.APPROVED.name), ReviewNode(
                            ReviewState.PENDING.name)
                    )), isDraft = true, commits = Commits(listOf(
                        CommitNode(Commit(CommitStatusCheckState("SUCCESS")))
                    ))
                ), "example1.com", VulnerabilityAlerts(arePresent = false)),
                Repository("example_repo_2", buildPullRequests(
                    author = author, year = 2012, review = Review(listOf(ReviewNode(ReviewState.CHANGES_REQUESTED.name)
                    )), isDraft = false, commits = Commits(listOf(
                        CommitNode(Commit(null))
                    ))
                ), "example2.com", VulnerabilityAlerts(arePresent = true)),
                Repository("example_repo_3", buildPullRequests(author, 2008,
                    isDraft = false, commits = Commits(listOf(
                    CommitNode(Commit(CommitStatusCheckState("FAILURE")))
                ))),
                    "example3.com", VulnerabilityAlerts(arePresent = false)),
                Repository("example_repo_4", buildPullRequests(author, 2013,
                    isDraft = false, commits = Commits(listOf(
                        CommitNode(Commit(CommitStatusCheckState("EXPECTED")))
                    ))),
                    "example4.com", VulnerabilityAlerts(arePresent = false)),
                Repository("example_repo_5", buildPullRequests(author, 2014,
                    isDraft = false, commits = Commits(listOf(
                        CommitNode(Commit(CommitStatusCheckState("PENDING")))
                    ))),
                    "example5.com", VulnerabilityAlerts(arePresent = false)),
                Repository("example_repo_6", buildPullRequests(author, 2015,
                    isDraft = false, commits = Commits(listOf(
                        CommitNode(Commit(CommitStatusCheckState("SUCCESS")))
                    ))),
                    "example6.com", VulnerabilityAlerts(arePresent = false)),
                Repository("example_repo_7", buildPullRequests(author, 2016,
                    isDraft = false, commits = Commits(listOf(
                        CommitNode(Commit(CommitStatusCheckState("UNEXPECTED_STATE")))
                    ))),
                    "example7.com", VulnerabilityAlerts(arePresent = false))
            )
        )

        val members = Members(listOf(MembersNode("example_team_member_1")))
        val team = Team("example_team", members, repositories)
        val data = Data(Organization(team))
        return GithubDashboardData(data)
    }

    private fun buildPullRequests(
        author: Author,
        year: Int,
        review: Review = Review(emptyList()),
        isDraft: Boolean,
        commits: Commits
    ) = PullRequests(
            listOf(
                PullRequestNode(
                    "https://example.com/1",
                    createZonedDateTime(year),
                    author,
                    "Add cool feature",
                    review,
                    isDraft,
                    commits
                )
            )
        )

    private fun createZonedDateTime(year: Int) =
        ZonedDateTime.of(year, 1, 1, 1, 1, 1, 0, systemDefault())

    @MockBean
    fun gitHubService() = gitHubService
}
