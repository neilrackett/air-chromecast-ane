package com.mesmotronic.ane.aircast;

import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.cast.RemoteMediaPlayer;
import com.google.android.gms.cast.RemoteMediaPlayer.MediaChannelResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.images.WebImage;

import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;

public class ChromecastMediaController 
{
	private RemoteMediaPlayer remote = null;
	
	public ChromecastMediaController(RemoteMediaPlayer mRemoteMediaPlayer) 
	{
		remote = mRemoteMediaPlayer;
	}
	
	public MediaInfo createLoadUrlRequest(String contentId, String contentType, long duration, String streamType, JSONObject metadata) 
	{
		// Try creating a GENERIC MediaMetadata obj
		MediaMetadata mediaMetadata = new MediaMetadata();
		
		try 
		{
			int metadataType = metadata.has("metadataType") 
				? metadata.getInt("metadataType") 
				: MediaMetadata.MEDIA_TYPE_MOVIE;
            
			// GENERIC
			if (metadataType == MediaMetadata.MEDIA_TYPE_GENERIC) 
			{
				mediaMetadata = new MediaMetadata(); // Creates GENERIC MediaMetaData
				mediaMetadata.putString(MediaMetadata.KEY_TITLE, (metadata.has("title")) ? metadata.getString("title") : "");
				mediaMetadata.putString(MediaMetadata.KEY_SUBTITLE, (metadata.has("title")) ? metadata.getString("subtitle") : "");
				mediaMetadata = addImages(metadata, mediaMetadata);
			}
			
		}
		catch(Exception e) 
		{
			e.printStackTrace();
			// Fallback
			mediaMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);
		}

    	int _streamType = MediaInfo.STREAM_TYPE_BUFFERED;
    	
    	if (streamType.equals("live")) 
    	{
    		_streamType = MediaInfo.STREAM_TYPE_LIVE;
    	}
    	else if (streamType.equals("other")) 
    	{
    		_streamType = MediaInfo.STREAM_TYPE_NONE;
    	}
    	
    	MediaInfo mediaInfo = new MediaInfo.Builder(contentId)
    	    .setContentType(contentType)
    	    .setStreamType(_streamType)
    	    .setStreamDuration(duration)
    	    .setMetadata(mediaMetadata)
    	    .build();
    	
    	return mediaInfo;
	}
	
	public void play(GoogleApiClient apiClient) 
	{
		PendingResult<MediaChannelResult> res = this.remote.play(apiClient);
//		res.setResultCallback(this.createMediaCallback(callback));
	}
	
	public void pause(GoogleApiClient apiClient) 
	{
		PendingResult<MediaChannelResult> res = this.remote.pause(apiClient);
//		res.setResultCallback(this.createMediaCallback(callback));
	}
	
	public void stop(GoogleApiClient apiClient)
	{
		PendingResult<MediaChannelResult> res = this.remote.stop(apiClient);
//		res.setResultCallback(this.createMediaCallback(callback));
	}
	
	public void seek(long seekPosition, String resumeState, GoogleApiClient apiClient) 
	{
		PendingResult<MediaChannelResult> res = null;
		
		if (resumeState != null && !resumeState.equals("")) 
		{
			if (resumeState.equals("PLAYBACK_PAUSE")) 
			{
				res = this.remote.seek(apiClient, seekPosition, RemoteMediaPlayer.RESUME_STATE_PAUSE);
			} 
			else if (resumeState.equals("PLAYBACK_START")) 
			{
				res = this.remote.seek(apiClient, seekPosition, RemoteMediaPlayer.RESUME_STATE_PLAY);
			}
			else 
			{
				res = this.remote.seek(apiClient, seekPosition, RemoteMediaPlayer.RESUME_STATE_UNCHANGED);
			}
		}
		
		if (res == null) 
		{
			res = this.remote.seek(apiClient, seekPosition);
		}
		
//		res.setResultCallback(this.createMediaCallback(callback));
	}
	
	public void setVolume(double volume, GoogleApiClient apiClient) 
	{
		PendingResult<MediaChannelResult> res = this.remote.setStreamVolume(apiClient, volume);
//		res.setResultCallback(this.createMediaCallback(callback));
	}
	
	public void setMuted(boolean muted, GoogleApiClient apiClient) 
	{
		PendingResult<MediaChannelResult> res = this.remote.setStreamMute(apiClient, muted);
//		res.setResultCallback(this.createMediaCallback(callback));
	}
	
	private ResultCallback<RemoteMediaPlayer.MediaChannelResult> createMediaCallback()
	{
		return new ResultCallback<RemoteMediaPlayer.MediaChannelResult>() 
		{
		    @Override
		    public void onResult(MediaChannelResult result) 
		    {
				if (result.getStatus().isSuccess()) 
				{
//					callback.onSuccess();
				}
				else 
				{
//					callback.onError("channel_error");
				}
		    }
		};
	}
    
    private MediaMetadata addImages(JSONObject metadata, MediaMetadata mediaMetadata) throws JSONException 
    {
        if (metadata.has("thumbnail")) 
        {
            String imageUrl = metadata.getString("thumbnail");
            
            if (imageUrl.indexOf("http") == 0) 
            {
	            Uri imageURI = Uri.parse( imageUrl );
	            WebImage webImage = new WebImage(imageURI);
	            mediaMetadata.addImage(webImage);
            }
        }
        
        return mediaMetadata;
    }
    
}