package org.edx.mobile.task;

import java.util.ArrayList;

import org.edx.mobile.model.db.DownloadEntry;
import org.edx.mobile.module.storage.IStorage;
import org.edx.mobile.module.storage.Storage;
import org.edx.mobile.player.TranscriptManager;

import android.content.Context;

public abstract class EnqueueDownloadTask extends Task<Long> {

    public EnqueueDownloadTask(Context context) {
        super(context);
    }

    @Override
    protected Long doInBackground(Object... params) {
        try {
            IStorage storage = new Storage(context);
            ArrayList<DownloadEntry> downloadList = (ArrayList<DownloadEntry>) params[0];
            if(downloadList!=null){
                int count = 0;
                for (DownloadEntry de : downloadList) {
                    try{
                        if(storage.addDownload(de)!=-1){
                            count++;
                        }
                        TranscriptManager transManager = new TranscriptManager(context);
                        transManager.downloadTranscriptsForVideo(de.transcript);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
                return (long)count;
            }
        } catch (Exception ex) {
            handle(ex);
        }
        return 0L;
    }
}
