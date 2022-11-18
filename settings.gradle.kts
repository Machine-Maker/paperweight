rootProject.name = "paperweight"

include("paperweight-core", "paperweight-lib", "paperweight-patcher", "paperweight-userdev", "paperweight-generator")

enableFeaturePreview("VERSION_CATALOGS")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
include("paperweight-common-tasks")
