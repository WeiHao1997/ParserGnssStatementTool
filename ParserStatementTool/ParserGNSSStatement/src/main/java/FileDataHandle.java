import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FileDataHandle {

    void fileDeal(String filePath) throws IOException {

        File readFile = new File(filePath);

        FileInputStream fileInputStream = new FileInputStream(filePath);

        int character = 0;

        while ((character = fileInputStream.read()) != -1){

            if( '$' == character){
                System.out.print(character);
            }

            if('*' == character){
                System.out.print(character);
            }

            System.out.print((char)character);
        }

    }

    void readCharHandle(int readChar){

    }
}
