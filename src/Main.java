import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class Main {
    static void readIndexFile(String fileName){
        try {
            RandomAccessFile file = new RandomAccessFile(fileName,"rw");
            for (int i = 1; i <=file.length()/4 ; i++) {
                System.out.print(" "+file.readInt());
                if (i !=0 && i%11==0)
                    System.out.println();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    static void CreateIndexFile(String fileName,int numberOfRecords,int m){
        try {
            RandomAccessFile file = new RandomAccessFile(fileName,"rw");
            for (int i = 0; i <numberOfRecords ; i++) {
                for (int j = 0; j <(m*2)+1 ; j++) {
                    if (j==1)
                    {
                        file.writeInt(i+1);
                    }
                    else
                    {
                        file.writeInt(-1);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String args[])
    {
        /*initialize project*/
        String indexFileName="src//index.bin";
        CreateIndexFile(indexFileName,10,5);
        readIndexFile(indexFileName);
    }
}
