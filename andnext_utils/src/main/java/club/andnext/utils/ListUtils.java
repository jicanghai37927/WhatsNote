package club.andnext.utils;

import java.util.List;

public class ListUtils {

    public static void move(List list, int from, int to) {
        list.add(to, list.remove(from));
    }
}
