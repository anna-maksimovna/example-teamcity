import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.triggers.vcs
import jetbrains.buildServer.configs.kotlin.vcs.GitVcsRoot

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2022.04"

project {

    vcsRoot(HttpsGithubComAnnaMaksimovnaExampleTeamcityGitRefsHeadsMaster)

    buildType(TestOrDeployToNexus)
}

object TestOrDeployToNexus : BuildType({
    name = "Test or Deploy to Nexus"

    artifactRules = "+:target/*.jar"

    vcs {
        root(HttpsGithubComAnnaMaksimovnaExampleTeamcityGitRefsHeadsMaster)
    }

    steps {
        maven {
            name = "Test step"

            conditions {
                doesNotContain("teamcity.build.branch", "master")
            }
            goals = "clean test"
            runnerArgs = "-Dmaven.test.failure.ignore=true"
        }
        maven {
            name = "Deploy step"

            conditions {
                contains("teamcity.build.branch", "master")
            }
            goals = "clean deploy"
            userSettingsSelection = "settings.xml"
        }
    }

    triggers {
        vcs {
        }
    }
})

object HttpsGithubComAnnaMaksimovnaExampleTeamcityGitRefsHeadsMaster : GitVcsRoot({
    name = "https://github.com/anna-maksimovna/example-teamcity.git#refs/heads/master"
    url = "https://github.com/anna-maksimovna/example-teamcity.git"
    branch = "refs/heads/master"
    branchSpec = "refs/heads/*"
    authMethod = password {
        userName = "anna-maksimovna"
        password = "credentialsJSON:5d34ec09-6f85-47b8-b117-51815c5fd2cf"
    }
})
