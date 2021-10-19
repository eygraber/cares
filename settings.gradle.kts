rootProject.name = "cares"

include(":core")
include(":portal")

include(":samples:name-and-count")
include(":samples:portal")
include(":samples:simple-portal")
include(":samples:torrents")

enableFeaturePreview("VERSION_CATALOGS")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
