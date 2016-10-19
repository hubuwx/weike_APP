package com.chenluwei.weike.db;

import com.chenluwei.weike.bean.DownloadFileInfo;
import com.chenluwei.weike.bean.ThreadInfo;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Created by lw on 2016/5/8.
 * 下载文件的数据库的数据访问接口
 */
public interface ThreadDAO {
    /**
     * 插入线程信息
     */
    public void insertThread(ThreadInfo threadInfo,DownloadFileInfo fileInfo,Integer progress);




    public void deleteThread(String url);

    /**
     * 更新线程下载进度
     * @param url
     * @param thread_id
     * @param finished
     */
    public void updateThread(String url,int thread_id,int finished,int progress);


    /**
     * 查询文件的线程信息
     * @param url
     * @return
     * @return List<ThreadInfo>
     */
    public List<ThreadInfo> getThreads(String url);


    /**
     *
     * @return
     */
    public List<Map<String,Object>>  getFileSimpleInfo();

    /**
     * 线程信息在数据库中是否已经存在
     * @param url
     * @param thread_id
     * @return
     */
    public boolean isExists(String url,int thread_id);
}
