
if (getBinding().variables.containsKey("updateClasspath")) return

updateClasspath = { classLoader = null ->
  // Add GWT libraries to compiler classpath.
  if (getBinding().variables.containsKey("gwtHome") || getBinding().variables.containsKey("gwtResolvedDependencies")) {
    if(getBinding().variables.containsKey("gwtHome")) {
      def gwtHomeFile = new File(gwtHome)
      if (gwtHomeFile.exists()) {
        // Update the dependency lists.
        new File(gwtHome).eachFileMatch(~/^gwt-(dev-\w+|user)\.jar$/) { File f ->
          grailsSettings.compileDependencies << f
          grailsSettings.testDependencies << f
          gwtDependencies << f
        }
        def gwtServlet = new File(gwtHomeFile, "gwt-servlet.jar")
        if (gwtServlet.exists()) {
          grailsSettings.runtimeDependencies << gwtServlet
        }
      }
    }
    grailsSettings.testDependencies << gwtClassesDir
    if (gwtLibFile.exists()) {
      gwtLibFile.eachFileMatch(~/.+\.jar$/) { f ->
        grailsSettings.testDependencies << f
        gwtDependencies << f
      }
    }
    if (buildConfig.gwt.use.provided.deps == true) {
      if (grailsSettings.metaClass.hasProperty(grailsSettings, "providedDependencies")) {
        grailsSettings.providedDependencies.each { dep ->
          grailsSettings.testDependencies << dep
          gwtDependencies << f
        }
      }
      else {
        ant.echo message: "WARN: You have set gwt.use.provided.deps, " +
                "but are using a pre-1.2 version of Grails. The setting " +
                "will be ignored."
      }
    }

    gwtResolvedDependencies.each { File f ->
      if (!f.name.contains("gwt-dev")) {

        rootLoader.addURL(f.toURL())

        if (classLoader) {
          classLoader.addURL(f.toURL())
        }
      }

      grailsSettings.compileDependencies << f
      grailsSettings.runtimeDependencies << f
      grailsSettings.testDependencies << f
      gwtDependencies << f
    }

  }
}