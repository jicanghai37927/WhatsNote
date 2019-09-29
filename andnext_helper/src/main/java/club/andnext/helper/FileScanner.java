package club.andnext.helper;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public class FileScanner implements ObservableOnSubscribe<List<File>> {

    List<File> list;
    FileFilter filter;

    List<File> result;

    public FileScanner(FileFilter filter, File... dirs) {
        this.list = Arrays.asList(dirs);
        this.filter = filter;

        this.result = new ArrayList<>();
    }

    @Override
    public void subscribe(ObservableEmitter<List<File>> emitter) throws Exception {

        for (File dir : list) {
            this.scan(dir);
        }

        emitter.onNext(result);
        emitter.onComplete();
    }

    void scan(File file) {
        if (!file.exists()) {
            return;
        }

        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null && files.length != 0) {
                for (File f : files) {
                    scan(f);
                }
            }

        } else {
            File pathname = file;
            if (filter.accept(pathname)) {
                result.add(file);
            }
        }
    }
}