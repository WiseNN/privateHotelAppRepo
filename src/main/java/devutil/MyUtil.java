package devutil;

import java.io.*;
import java.util.Base64;
import java.util.Base64.*;

public class MyUtil
{

    public String serializeObject(Object object) throws IOException, ClassNotFoundException
    {

        try {


            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            ObjectOutputStream objOut = new ObjectOutputStream(byteOut);
            objOut.writeObject(object);
            objOut.close();
            byteOut.close();
            byte[] bytes = byteOut.toByteArray();



            return Base64.getEncoder().encodeToString(bytes);
        }catch( Exception e)
        {
            System.out.println(e.getLocalizedMessage());
        }

        return null;
    }

    public <T extends Serializable> T deserializeObject(Class<T> type, String encodedString) throws IOException, ClassNotFoundException
    {
//        if(bytes == null)
//        {
//            return null;
//        }

//        ByteArrayInputStream byteIn = new ByteArrayInputStream(bytes);
//        ObjectInputStream in = new ObjectInputStream(byteIn);
//        T obj = (T) in.readObject();
//        in.close();

        byte b[] = Base64.getDecoder().decode(encodedString.getBytes());
        ByteArrayInputStream bi = new ByteArrayInputStream(b);
        ObjectInputStream si = new ObjectInputStream(bi);
        T object = (T)si.readObject();


        return object;
    }
}
