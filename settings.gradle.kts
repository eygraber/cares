rootProject.name = "cares"
include(":core")
include(":samples:simple-clock:desktop-app")
findProject(":samples:simple-clock:desktop-app")?.name = "desktop-app"
include("samples:simple-clock:app")
findProject(":samples:simple-clock:app")?.name = "app"
include("nav")

enableFeaturePreview("VERSION_CATALOGS")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
