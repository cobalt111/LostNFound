package com.timothycox.lostnfound;


import android.content.Context;

import com.timothycox.lostnfound.utilities.Database;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DatabaseUnitTest {

    private Database mDatabase = Database.getInstance();

    @Mock
    Context mContext;

    @Test
    public void confirmDatabaseIsSingleton() {
        Database testDatabase = Database.getInstance();
        Assert.assertSame(mDatabase, testDatabase);
    }

}
