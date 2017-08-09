# jackknife

allprojects {
  repositories {
    jcenter()
    <b>maven { url "https://jitpack.io" }</b>
  }
}

dependencies {
  <b>compile 'com.github.JackWHLiu:jackknife:1.0.0'</b>
}
