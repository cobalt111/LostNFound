package com.example.tim.lostnfound;


import android.content.Context;
import android.util.Log;

import com.example.tim.lostnfound.Utilities.FileUtils;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class FileUtilsUnitTest {

    @Mock
    Context mContext;

    @Test
    public void confirmFilePath() {
        final String STORAGE_FILENAME = "lostNfound-animals.json";
        Assert.assertSame(FileUtils.filePath(mContext), STORAGE_FILENAME);
    }

    @Test
    public void confirmFileCreation() {
        Assert.assertTrue(FileUtils.createFile(mContext));
    }


    @Test
    public void confirmFileWrite() {
        List<String> emptyList = new ArrayList<>();
        Assert.assertTrue(FileUtils.writeToFile(emptyList, mContext));
    }

    @Test
    public void confirmFileRead() {

    }
}
