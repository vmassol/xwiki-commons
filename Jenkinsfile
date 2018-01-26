
node {
  def currentBuild = xwikiBuild {
    goals = "clean package"
    xvnc = false
    properties = "-N"
  }

  echo "Result = ${currentBuild.result}"
  if (currentBuild.result == 'SUCCESS') {
    build job: "../xwiki-rendering/${env.BRANCH_NAME}", wait: false
  }    
}


