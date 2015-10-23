# SubtitleSeeker
Fast, lightweight subtitles for VideoViews using just a TextView.
After searching for 5 hours on Google looking for an easy implmentation of subtitles for the native Android VideoView. I gave up and wrote my own!
All SubtitleSeeker requires is a VideoView to sync with and a TextView to display the subtitles on. Implementation can be done in under a few seconds.
PLEASE ALSO READ THE NOTE BELOW

## NOTE ##
Subtitle Seeker does not support subtitle styles, fonts, postitioning and animations. Only use SubtitleSeeker if your only goal is getting text in subtitles from file to screen, fast!

## Example ##
```java
//Get views
final VideoView videoView = findViewById(R.id.video);
final TextView subtitleView = findViewById(R.id.subtitles);
//Initialize SubtitleSeeker
SubtitleSeeker ss = new SubtitleSeeker(videoView);
//Parse subtitle
TimedTextObject tto = new FormatASS().parseFile(new File(getCacheDir(), "subtitle.ass");
//Tell SubtitleSeeker to start working
ss.sync(tto);
//Play video!
videoView.play();
```

## Screenshot ##
![Screenshot](http://files.nulldev.xyz/Images/1.png)

## Credits ##
SubtitleSeeker uses 'subtitleConverter'. You can view the project here: https://github.com/JDaren/subtitleConverter