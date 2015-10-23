package xyz.nulldev.subtitles;

import android.widget.TextView;
import android.widget.VideoView;
import subtitleFile.Caption;
import subtitleFile.TimedTextObject;
import xyz.nulldev.util.ConcurrencyUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Project: SubtitleSeeker
 * Created: 22/10/15
 * Author: nulldev
 */

/**
 * Fast, reliable and stable subtitles for VideoView using just a TextView!
 */
public class SubtitleSeeker {

    int delay = 250;

    int prevTime = 0;
    VideoView view = null;
    TimedTextObject textObject;
    TextView subView = null;
    Timer timer;
    ArrayList<Caption> captionList = new ArrayList<>();

    /**
     * Construct a subtitle seeker
     * @param view The video view to sync with
     * @param subView The text view to display subtitles on
     */
    public SubtitleSeeker(VideoView view, TextView subView) {
        this.view = view;
        this.subView = subView;
    }

    /**
     * Get the TextView to display subtitles on.
     * @return The TextView to display subtitles on.
     */
    public TextView getSubView() {
        return subView;
    }

    /**
     * Set the TextView to display subtitles on.
     * @param subView The TextView to display subtitles on.
     */
    public void setSubView(TextView subView) {
        this.subView = subView;
    }

    /**
     * Get the VideoView to sync the subtitles with
     * @return The VideoView to sync the subtitles with
     */
    public VideoView getView() {
        return view;
    }

    /**
     * Get how often in milliseconds to refresh the subtitles
     * @return How often in milliseconds to refresh the subtitles
     */
    public int getDelay() {
        return delay;
    }

    /**
     * Set how often in milliseconds to refresh the subtitles
     * @param delay How often in milliseconds to refresh the subtitles
     */
    public void setDelay(int delay) {
        this.delay = delay;
    }

    /**
     * Start syncing a subtitle object with the VideoView
     * @param textObject The subtitle to load
     */
    public void sync(TimedTextObject textObject) {
        this.textObject = textObject;

        //Create caption list
        captionList.addAll(textObject.captions.values());
        //Schedule sync task
        timer = new Timer();
        timer.schedule(new SyncTask(), 0, delay);
    }

    /**
     * Stop syncing the subtitle object with the VideoView
     */
    public void deSync() {
        //Cancel display task
        if(timer != null) {
            timer.cancel();
        }
        this.prevTime = 0;
        this.textObject = null;
        this.captionList.clear();
    }

    /**
     * Update the TextView with new captions.
     * @param text The new text to set the TextView to
     */
    void updateTextView(final String text) {
        //setText() is a slow procedure, run it only if we need to
        if (!subView.getText().equals(text)) {
            ConcurrencyUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    subView.setText(text);
                }
            });
        }
    }

    /**
     * Async task to keep the TextView and the VideoView in sync
     */
    public class SyncTask extends TimerTask {

        @Override
        public void run() {
            if (captionList != null) {
                //Get the current position
                int currentPosition = view.getCurrentPosition();
                //Check if the user is seeking around
                if(currentPosition < prevTime) {
                    //Re-add all the removed subtitles
                    captionList.clear();
                    captionList.addAll(textObject.captions.values());
                }
                //We may have multiple captions so a StringBuilder is more faster and suitable
                StringBuilder captionBuilder = new StringBuilder();
                //Go through all captions to display
                Iterator<Caption> captionIterator = captionList.iterator();
                while (captionIterator.hasNext()) {
                    Caption caption = captionIterator.next();
                    if (caption.start.getMseconds() <= currentPosition && caption.end.getMseconds() >= currentPosition) {
                        //Append the caption
                        if (captionBuilder.length() > 0) {
                            captionBuilder.append('\n');
                        }
                        captionBuilder.append(caption.content);
                    } else {
                        //Remove captions behind our current position
                        if(caption.start.getMseconds() < currentPosition && caption.end.getMseconds() < currentPosition) {
                            captionIterator.remove();
                        }
                    }
                }
                prevTime = currentPosition;
                final String caption = captionBuilder.toString();
                //Update the textview
                updateTextView(caption);
            }
        }
    }
}
