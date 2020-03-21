import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class Main {

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
    int InsertNewRecordAtIndex (String filename, int RecordID, int Reference){return 0;}
    void DeleteRecordFromIndex (String filename, int RecordID){}
    static int SearchARecord (String filename, int RecordID){
        try {
            RandomAccessFile file= new RandomAccessFile(filename,"rw");
            file.seek(11*4);
            while (true){
                int detector = file.readInt();
                if (detector==1)
                {
                    for (int i = 0; i <11 ; i++) {
                        int reader = file.readInt();
                        if (reader>=RecordID)
                        {
                            int x = file.readInt();
                            file.seek(x*44);
                            break;
                        }
                    }
                }
                else
                {
                    for (int i = 0; i <5 ; i++) {
                        int number = file.readInt();
                        if (number==RecordID && number!=-1)
                        {
                            System.out.println(RecordID +"founded with file reference "+file.readInt());
                            return 0;
                        }
                        else
                        {
                            file.readInt();
                        }
                    }
                    System.out.println(RecordID+" no founded!!");
                    return 0;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;}
    static void DisplayIndexFileContent(String fileName){
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
    public static void fill(String path){
        try {
            RandomAccessFile file= new RandomAccessFile(path,"rw");
            int[] arr = {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
                    1,10,8,32,9,-1,-1,-1,-1,-1,-1,
                    0,1,120,2,144,3,12,-1,-1,-1,-1,
                    0,11,192,14,72,12,204,15,108,-1,-1,
                    0,5,132,6,180,7,24,-1,-1,-1,-1,
                    0,8,156,9,168,10,48,-1,-1,-1,-1,
                    0,17,216,18,228,19,84,-1,-1,-1,-1,
                    0,24,60,30,196,32,240,-1,-1,-1,-1,
                    1,3,2,7,4,10,5,-1,-1,-1,-1,
                    1,15,3,19,6,32,7,-1,-1,-1,-1};

            for (int i = 0; i <arr.length ; i++)
                file.writeInt(arr[i]);
            file.seek(0);
            while (file.getFilePointer()!=(file.length()))
                System.out.println(file.readInt());
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
       // CreateIndexFile(indexFileName,10,5);
       // DisplayIndexFileContent(indexFileName);
        fill(indexFileName);
        SearchARecord(indexFileName,5);
        // CreateIndexFile(indexFileName,10,5);
        // DisplayIndexFileContent(indexFileName);
    }
}


