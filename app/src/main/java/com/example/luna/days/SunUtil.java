package com.example.luna.days;

import android.os.Environment;

import java.io.File;

/**
 * Created by LUNA on 2017-09-11.
 */

public class SunUtil {

    public static String makeDir(String dirName)
    {
        //Environment: Provides access to environment variables.
        //ㄴgetExternalStorageDirectory(): Return the primary shared/external storage directory.
        //File>getAbsoluteFile(): Returns the absolute form of this abstract pathname.
        String mRootPath = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator+dirName;

        // File.pathSeparator는 파일의 경로에서 각각의 파일들을 분리해주는 것.
        // 예를 들어 윈도우 환경변수의 경로를 설정할때 여러개의 파일경로들을 ;로 구분을 하는데,
        // File.pathSeparator는 ;로 파일들을 구분
        //File.separator는 \, /같은 파일의 경로를 분리해주는 메소드.
        // 윈도우에서는 C:\Documents\Test이런 경로를 각각 구분해주는 역할
        /*
        만약 Data 밑에 Tweet.txt라는 파일을 원한다고 할 때,  윈도우는 "Data\\"Tweet.txt" 리눅스는 "Data/Tweet.txt" 형식.
        그러나 자바에서는  "Data" + File.separator + "Tweet.txt"라고 쓰면 됨.
        출처: http://shineware.tistory.com/entry/시스템에-따라-다른-Fileseparator [To. Me]*/

        try {
            File file = new File(mRootPath);
        /*File (String pathname)
        *Creates a new File instance by converting the 'given pathname string' into an abstract pathname.
        * If the given string is the empty string, then the result is the empty abstract pathname.
        * */
            if (file.exists()==false)
            {
                if(file.mkdirs()==false)
                /*
                * boolean	mkdir(): Creates the directory named by this abstract pathname.
                * 만들고자 하는 디렉토리의 상위 디렉토리가 존재하지 않을 경우, 생성 불가
                 C:\base\want--> want 디렉토리를 만들고자 하는데, base 디렉토리가 없는 경우, 생성 불가
                * boolean	mkdirs(): Creates the directory named by this abstract pathname,
                *                   including any necessary but nonexistent parent directories.
                *                   만들고자 하는 디렉토리의 상위 디렉토리가 존재하지 않을 경우, 상위 디렉토리까지 생성
                                    C:\base\want--> want 디렉토리를 만들고자 하는데, base 디렉토리가 없는 경우, base 디렉토리까지 생성

                * */
                {
                    throw new Exception("");
                }//if(fRoot.mkdirs()==false)
            }//if (fRoot.exists()==false)
        } catch (Exception e) {
            e.printStackTrace();
            mRootPath="-1";
        }
        return mRootPath+"/";

    }//makeDir


    public static void removeDir(String dirName)
    {//TODO 삭제 기능되게 하기
        //String mRootPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + dirName;

        SunUtil.makeDir("dirName");

        File file = new File(SunUtil.makeDir("dirName"));
        if(file.exists()==true)
        {
            file.delete();    //root 삭제
        }
/*        File[] childFileList = file.listFiles();
        for(File childFile : childFileList)

        {
            if(childFile.isDirectory()) {
                removeDir(childFile.getAbsolutePath());    //하위 디렉토리
            }
            else {
                childFile.delete();    //하위 파일
            }
        }*/


    }//removeDir
}
