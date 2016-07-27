package io.github.mkjung.ivi.media;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import org.videolan.libvlc.util.AndroidUtil;
//import io.github.mkjung.ivi.PlaybackService;
import io.github.mkjung.ivi.R;
import io.github.mkjung.ivi.VLCApplication;
// import io.github.mkjung.ivi.gui.video.VideoPlayerActivity;
import io.github.mkjung.ivi.util.FileUtils;
import io.github.mkjung.ivi.util.Strings;
import io.github.mkjung.ivi.util.SubtitlesDownloader;
import io.github.mkjung.ivi.util.Util;

import java.util.ArrayList;
import java.util.List;

public class MediaUtils {
    public static final String ACTION_SCAN_START = Strings.buildPkgString("gui.ScanStart");
    public static final String ACTION_SCAN_STOP = Strings.buildPkgString("gui.ScanStop");

    private static SubtitlesDownloader sSubtitlesDownloader;

    public static void actionScanStart() {
        Intent intent = new Intent();
        intent.setAction(ACTION_SCAN_START);
        LocalBroadcastManager.getInstance(VLCApplication.getAppContext()).sendBroadcast(intent);
    }

    public static void getSubs(Activity activity, ArrayList<MediaWrapper> mediaList) {
        getSubs(activity, mediaList, null);
    }

    public static void getSubs(Activity activity, ArrayList<MediaWrapper> mediaList, SubtitlesDownloader.Callback cb) {
        if (sSubtitlesDownloader == null)
            sSubtitlesDownloader = new SubtitlesDownloader();
        sSubtitlesDownloader.setActivity(activity);
        sSubtitlesDownloader.downloadSubs(mediaList, cb);
    }

    public static void getSubs(Activity activity, MediaWrapper media, SubtitlesDownloader.Callback cb) {
        ArrayList<MediaWrapper> mediaList = new ArrayList<>();
        mediaList.add(media);
        getSubs(activity, mediaList, cb);
    }

    public static void getSubs(Activity activity, MediaWrapper media) {
        ArrayList<MediaWrapper> mediaList = new ArrayList<>();
        mediaList.add(media);
        getSubs(activity, mediaList);
    }

    public static void updateSubsDownloaderActivity(Activity activity) {
        if (sSubtitlesDownloader != null)
            sSubtitlesDownloader.setActivity(activity);
    }

    public static void actionScanStop() {
        Intent intent = new Intent();
        intent.setAction(ACTION_SCAN_STOP);
        LocalBroadcastManager.getInstance(VLCApplication.getAppContext()).sendBroadcast(intent);
    }

    public static String getMediaArtist(Context ctx, MediaWrapper media) {
        final String artist = media.getArtist();
        return artist != null ? artist : getMediaString(ctx, R.string.unknown_artist);
    }

    public static String getMediaReferenceArtist(Context ctx, MediaWrapper media) {
        final String artist = media.getReferenceArtist();
        return artist != null ? artist : getMediaString(ctx, R.string.unknown_artist);
    }

    public static String getMediaAlbumArtist(Context ctx, MediaWrapper media) {
        final String albumArtist = media.getAlbumArtist();
        return albumArtist != null ? albumArtist : getMediaString(ctx, R.string.unknown_artist);
    }

    public static String getMediaAlbum(Context ctx, MediaWrapper media) {
        final String album = media.getAlbum();
        return album != null ? album : getMediaString(ctx, R.string.unknown_album);

    }

    public static String getMediaGenre(Context ctx, MediaWrapper media) {
        final String genre = media.getGenre();
        return genre != null ? genre : getMediaString(ctx, R.string.unknown_genre);
    }

    public static String getMediaSubtitle(MediaWrapper media) {
        String subtitle = media.getNowPlaying() != null
                ? media.getNowPlaying()
                : media.getArtist();
        if (media.getLength() > 0L) {
            if (TextUtils.isEmpty(subtitle))
                subtitle = Strings.millisToString(media.getLength());
            else
                subtitle = subtitle + "  -  " +  Strings.millisToString(media.getLength());
        }
        return subtitle;
    }

    public static String getMediaTitle(MediaWrapper mediaWrapper){
        String title = mediaWrapper.getTitle();
        if (title == null)
            title = FileUtils.getFileNameFromPath(mediaWrapper.getLocation());
        return title;
    }

    public static Uri getContentMediaUri(Uri data) {
        Uri uri = null;
        try {
            Cursor cursor = VLCApplication.getAppContext().getContentResolver().query(data,
                    new String[]{ MediaStore.Video.Media.DATA }, null, null, null);
            if (cursor != null) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
                if (cursor.moveToFirst())
                    uri = AndroidUtil.PathToUri(cursor.getString(column_index));
                cursor.close();
            } else // other content-based URI (probably file pickers)
                uri = data;
        } catch (Exception e) {
            uri = data;
            if (uri.getScheme() == null)
                uri = AndroidUtil.PathToUri(uri.getPath());
        }
        return uri != null ? uri : data;
    }
    private static String getMediaString(Context ctx, int id) {
        if (ctx != null)
            return ctx.getResources().getString(id);
        else
            return "";

//        else {
//            switch (id) {
//                case R.string.unknown_artist:
//                    return "Unknown Artist";
//                case R.string.unknown_album:
//                    return "Unknown Album";
//                case R.string.unknown_genre:
//                    return "Unknown Genre";
//                default:
//                    return "";
//            }
//        }
    }
}
