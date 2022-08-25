/*
Author: Ditlev Frickmann

I've been using multibranch pipelines in Jenkins to carry out continous integration during my work at Phase One.
The scope of the pipelines that I've built do everything from building and publishing docker images for testing,
to run tests on physical and software infrastructures.

Additionally, I've written some shared libraries in groovy that I use in the pipelines.
*/

pipeline
{
    agent {
        label 'curriculum-vitae-builders'
    }

    stages
    {
        stage('SCM')
        {
            steps
            {
                checkout([
                    $class: 'GitSCM',
                    branches: [[name: env.BRANCH_NAME]],
                    extensions: [],
                    userRemoteConfigs: [[credentialsId: 'skrrrlev', url: 'https://github.com/skrrrlev/curriculum-vitae']]
                    ]
                )
                // Clean the repository after checkout.
                bat(
                    script: '''
                            git clean -fdx
                            ''',
                    label: 'Cleaning git directory'
                )
            }
        }

        stage('Linux builder')
        {
            steps
            {
                script
                {
                    // Get the Dockerfiles using the findFiles from the Pipeline Utility Steps plugin
                    def dockerFiles = findFiles(
                        glob: 'Dockerfiles/**/*.Dockerfile'
                    )

                    // Build a map for building the images in parallel
                    def parallelBuildMap = dockerFiles.collectEntries
                    {
                        ["${it}": buildDockerImageOnWindows(it)]
                    }

                    // build the images in parallel
                    parallel parallelBuildMap
                }
            }
        }
    }
}

def buildDockerImageOnWindows(dockerFile)
{
    return {
        script 
        {
            // extract the basename of the file
            // (remove path and extension)
            String baseName = dockerFile.getName().substring(0,dockerFile.getName().lastIndexOf('.'))

            // Create the stage for building the <dockerFile>
            stage("${baseName}")
            {
                println "Building ${baseName}"
                // Create the badge that displays the status of the builder
                def badge = addEmbeddableBadgeConfiguration(id: "${baseName}", subject: "${baseName}")
                
                // Create a tag that is applied to the image
                // It is not used to retrieve the image as of now, so it only needs to be unique for each unique case (branch, target).
                String tag = env.BRANCH_NAME.replaceAll('/','-')

                try 
                {
                    badge.setStatus('running')
                    // build the image from the <dockerFile>
                    bat(
                        script: "docker build . -f ${dockerFile.toString()} --target ${baseName} -t ${baseName}:${tag} --no-cache",
                        label: 'Building image'
                    )
                    
                    badge.setStatus('passing')
                }
                catch (Exception e)
                {
                    badge.setStatus('failing')
                    throw e
                }
            }
        }
    }
}