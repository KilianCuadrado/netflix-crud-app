import java.util.Properties

val secretProps = Properties()
val secretFile = project.rootProject.file("secrets.properties")
if (secretFile.exists()) {
    secretProps.load(secretFile.inputStream())
}

tasks.withType<JavaExec> {
    // Passem les propietats de Gradle a la JVM de l'aplicació
    if (secretFile.exists()) {
        systemProperty("SUPABASE_URL", secretProps.getProperty("supabase.url"))
        systemProperty("SUPABASE_USER", secretProps.getProperty("supabase.user"))
        systemProperty("SUPABASE_PASS", secretProps.getProperty("supabase.pass"))
        systemProperty("SUPABASE_PORT", secretProps.getProperty("supabase.port"))
    }
}

plugins {
    kotlin("jvm") version "2.2.0"
    application // Necessari per executar la GUI fàcilment
    id("org.openjfx.javafxplugin") version "0.1.0" // Versió actualitzada
}

javafx {
    version = "20" // Ha de coincidir o ser propera al teu JDK
    modules("javafx.controls", "javafx.fxml") // "fxml" és imprescindible per al patró MVC
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.xerial:sqlite-jdbc:3.36.0.3")
    implementation("org.postgresql:postgresql:42.7.3")
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("MainKt") // El punt d'entrada de la App
}

tasks.test {
    useJUnitPlatform()
}