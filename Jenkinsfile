pipeline
{
    agent
    {
        label 'linux-docker-host-slave'
    }
    tools
    {
        maven 'mvn350'
        jdk 'jdk8u131'
    }
    stages
    {
        stage("run tests")
        {
            steps
            {
                script
                {
                    def testImage = docker.build("test-image", "docker")
                    def splits = splitTests parallelism: [$class: 'CountDrivenParallelism', size: 4], generateInclusions: true

                    def testGroups = [:]


                    for (int i = 0; i < splits.size(); i++)
                    {
                        def index=i
                        def split = splits[index]
                        echo "*** index=${index}"
                        echo "*** split.includes=${split.includes}"
                        echo "*** split.list=${split.list}"

                        testGroups["split-${index}"] =
                        {  // example, "split3"
                            testImage.inside("-v /data/jenkins_home/.m2:/data/jenkins_home/.m2:rw -v /data/jenkins_home/jenkins_slave/tools/:/data/jenkins_home/jenkins_slave/tools")
                            {
                                sh "/data/jenkins_home/jenkins_slave/tools/hudson.model.JDK/jdk8u131/bin/java -version"
                                checkout scm

                                def mavenInstall = 'install -Dcsi.disable.nightlybuild=false -Dskip.junit.tests=true -DMaven.test.failure.ignore=true'

                                sh "rm -rf target **/target"
                                sh "mkdir -p target"
                                sh "pwd"
                                if (split.includes) {
                                    writeFile file: "target/parallel-test-includes-${index}.txt", text: split.list.join("\n")
                                    mavenInstall += " -Dsurefire.includesFile=target/parallel-test-includes-${index}.txt"
                                } else {
                                    writeFile file: "target/parallel-test-excludes-${index}.txt", text: split.list.join("\n")
                                    mavenInstall += " -Dsurefire.excludesFile=target/parallel-test-excludes-${index}.txt"
                                }


                                withEnv(["JAVA_HOME=${tool 'jdk8u131' }", "PATH+MAVEN=${tool 'mvn350'}/bin:${env.JAVA_HOME}/bin"]) {        /* Call the Maven build with tests. */
                                    sh "ls target/parallel-test* "
                                    sh "cat target/parallel-test-*-${index}.txt "
                                    sh "mvn ${mavenInstall}"
                                }
                            }
                        }
                    }
                    try
                    {
                        parallel testGroups
                    }
                    catch(error)
                    {
                    }
                }
            }
        }
    }
    post
    {
        always
        {
            junit '**/target/surefire-reports/TEST-*.xml'
            archive includes: '**/target/surefire-reports/TEST-*.xml'
            archive includes: '**/parallel-test-*.txt'
        }
    }
}


