package cn.gz.rd.datacollection;

import cn.gz.rd.datacollection.utils.PoiUtils;

import java.io.File;
import java.io.FileOutputStream;

public class Test {

    @org.junit.Test
    public void test() throws Exception {
        File file = new File("C:\\Users\\panxuan\\Desktop\\市农科院承担的“珠江河口蔬菜品种创新及精准栽培技术研究与示范应用”项目通过成果评价.doc");
        byte[] bytes = PoiUtils.word2003ToHtml(file);
        FileOutputStream fileOutputStream = new FileOutputStream("C:\\Users\\panxuan\\Desktop\\test.html");
        fileOutputStream.write(bytes);

    }


}
