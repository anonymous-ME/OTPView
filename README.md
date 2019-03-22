# Android PinView / OtpView

Custom view that contains EditTexts for typing and X number of digits depending on the set pin length.

## How to integrate the library in your app? ##

Add it in your root build.gradle at the end of repositories:
```groovy
allprojects {
	repositories {
		maven { url "https://jitpack.io" }
	}
}
```
Step 2. Add the dependency
```groovy
dependencies {
	implementation 'com.github.anonymous-ME:OTPView:0.1.0'
}
```
Step 3. Add OTPView to your layout file
```xml
    <affan.ahmad.otp.OTPView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"/>
```
## Screenshot ##
![Screenshot](screen_shot.gif)
