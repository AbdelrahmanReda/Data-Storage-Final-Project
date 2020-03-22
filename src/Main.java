import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class Main {

    static void CreateIndexFile(String fileName,int numberOfRecords,int m){
        
        try {
            int counter = 0 ;
            RandomAccessFile file = new RandomAccessFile(fileName,"rw");
            for (int i = 0; i <numberOfRecords ; i++) {
                for (int j = 0; j <(m*2)+1 ; j++) {
                    if (j==1)
                    {
                        counter++;
                        file.writeInt(i+1);
                    }
                    else
                    {
                        counter++;
                        file.writeInt(-1);
                    }
                }
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
   
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
                  System.out.println("file length is "+file.length());
            
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
    
           
            
            
         /*   file.seek(0);
            while (file.getFilePointer()!=(file.length()))
                System.out.println(file.readInt());*/
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void inertNode(RandomAccessFile file,int key , int ref,int pointer){
    
        System.out.println("ref is "+pointer);
           try {
             
               while ((int)file.getFilePointer()%44!=0)
               {
                  // System.out.println("@@@@@@"+file.readInt());
                   int tempKey = file.readInt();    //
                   int tempRef = file.readInt();    //
                   System.out.println("tempKey "+tempKey);
                   System.out.println("tempRef "+tempRef);
                   file.seek(file.getFilePointer()-8); //step 8 step backward
                   file.writeInt(key);
                   file.writeInt(ref);
                   key = tempKey;
                   ref=tempRef;
               }
           } catch (IOException e) {
               e.printStackTrace();
           }
    
       }
    
       static int getNextFreeRecord(RandomAccessFile file){
           try {
               file.seek(4);
               return file.readInt();
           } catch (IOException e) {
               e.printStackTrace();
               return -1;
           }
       }
    public static void splitNode(RandomAccessFile file,int targetRecordIndex )
    {
        
        try {
            file.seek(targetRecordIndex*44);
            file.writeInt(1); //convert the record to referential record .
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void updateFirstFreeRecord(RandomAccessFile file,int nextFree){
        try {
            file.seek(4);
            file.writeInt(nextFree);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void cordinateNode( int recordNumber, int key,int referece) throws FileNotFoundException {
            RandomAccessFile file = new RandomAccessFile("src/index.bin","rw");
           
            /*checking the record is full */
        try {
            int memorizer = (int)file.getFilePointer();
            file.seek(((recordNumber+1)*44)-4); //checkking the last number of the record
           // System.out.println("last numbe in the record is "+);
            if (file.readInt()!=-1)
            {
                
                //inserting into a full record
               // updateFirstFreeRecord(file,recordNumber+1);
                System.out.println("splitting . . . .");
                
            }
            else
            {
                //------
    
                file.seek(memorizer);
                try {
                    file.seek((recordNumber*44));
                    int y = file.readInt();
                    if (y == -1)
                    {
                        file.seek(file.getFilePointer()-4);
                        file.writeInt(0);
                        System.out.println("firt insertion to the record");
                        file.writeInt(key);
                        file.writeInt(referece);
                    }
                    else {
                        while (true)
                        {
                            int x = file.readInt();
                            System.out.println("x is "+x+" with index ");
                            if (key<x)
                            {
                                file.seek(file.getFilePointer()-4);
                                inertNode(file,key,referece,(int)(file.getFilePointer()));
                                break;
                            }
                            else if (x == -1)
                            {
                                System.out.println("file.getFilePointer() ::"+file.getFilePointer());
                                file.seek(file.getFilePointer()-4);
                                file.writeInt(key);
                                file.writeInt(referece);
                                break;
                            }
                            else
                            {
                                file.readInt();
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    static int InsertNewRecordAtIndex (String filename, int RecordID, int Reference){
    
        try {
            RandomAccessFile file = new RandomAccessFile(filename,"rw");
            file.seek(4);
            int firstFreeRecord= file.readInt();
            System.out.println("firstFreeRecord "+firstFreeRecord);
            cordinateNode(firstFreeRecord,RecordID,Reference);
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    
        return 0;
    }
    
    
    public static void main(String args[])
    {
        
        /*initialize project*/
        String indexFileName="src//index.bin";
        CreateIndexFile(indexFileName,10,5);
        DisplayIndexFileContent(indexFileName);
        InsertNewRecordAtIndex(indexFileName,1,30);
        InsertNewRecordAtIndex(indexFileName,5,34);
        InsertNewRecordAtIndex(indexFileName,11,342);
        InsertNewRecordAtIndex(indexFileName,6,52);
        InsertNewRecordAtIndex(indexFileName,2,123);
        InsertNewRecordAtIndex(indexFileName,30,675);
    
        DisplayIndexFileContent(indexFileName);
    
       
        //fill(indexFileName);
        //SearchARecord(indexFileName,5);
        //CreateIndexFile(indexFileName,10,5);
        // DisplayIndexFileContent(indexFileName);
    }
}


