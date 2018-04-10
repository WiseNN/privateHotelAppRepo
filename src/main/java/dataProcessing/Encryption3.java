package dataProcessing;

import devutil.ConsoleColors;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;


public class Encryption3
{



   public String[] encryptValue(String doWhat, String stringValue)
   {
      if (stringValue.equals("") || stringValue.equals(null))
      {
         System.out.println(ConsoleColors.yellowText("Param: doWhat MUST HAVE A VALUE!"));
         return null;
      }

      //create arrayList for randomNumber cipher ary for encrypting / decrypting
      ArrayList<Integer> randomNumsAry = new ArrayList<Integer>();
      String decryptKey;

      if (doWhat.equals("encrypt"))
      {

         //generate random array of numbers for stringValue
         int[] randNumsAryForStringValue = ranz(stringValue);


         //send random array of numbers for message, and text from message to encrypt function, store encrypted text
         String encryptedValue = encrypt(stringValue, randNumsAryForStringValue);

         //map array to decrypt string
         decryptKey = mapAryToString(randomNumsAry);

         //0 index=encryptedValue, 1 index=decryptKey
         return new String[]{encryptedValue, decryptKey};

      }
      System.out.println(ConsoleColors.redText("encryptValue() ERROR, PLEASE INVESTIGATE!"));
      return null;

   }

   public String decryptValue(String doWhat, String stringValue, String decryptKey)
   {

      if(stringValue.equals("") || decryptKey.equals(""))
      {
         System.out.println(ConsoleColors.yellowText("CANNOT PASS NULL FOR ANY PARAMETER"));
         return null;
      }
      if (doWhat.equals("decrypt"))
      {
         ArrayList<Integer> randonNumsAryFromClient = mapStringToAry(decryptKey);

         //get random numbers from randomNumsAry for recipientId & store in int variable
         int valueLength = stringValue.length();

         //pass length of returned array, and the client array to generate a short array of nums representing the recipientId
         int[] decryptAryForRecipientId = arrayGen(valueLength, randonNumsAryFromClient);

         System.out.println("decrypt key for recipientId: " + Arrays.toString(decryptAryForRecipientId));

         //decrypt the recipientId
         String decryptedStringValue = decrypt(stringValue, decryptAryForRecipientId);

         System.out.println("decrypted String: " + decryptedStringValue);

         String result = decryptedStringValue;

         //return decrypted value
         return result;
      }
      System.out.println(ConsoleColors.redText("decryptValue() ERROR, PLEASE INVESTIGATE!"));
      return null;
   }


	   /*
    *
    *
{
"_id": "WiseN",
"__v": 4,
"privateConvos": [
  {
     "recipientId": "TaslimD", (***ENCRYPT ***)
     "_id": "WiseN",
     "__v": 0,
     "messages": [
        {
           "_id": "5a130b5ac9b53d2d73e10952",
           "text": "Hey Whats Up", (***ENCRYPT ***)
           "sender": "WiseN",
           "time": " 12:05:29 GMT-0500 (EST)",
           "date": "October 20, 2017"
        },
        {
           "_id": "5a130c2fc9b53d2d73e10953",
           "text": "Hey, Nothing much, this API has really been kicking my ass", (***ENCRYPT ***)
           "sender": "TaslimD",
           "time": " 12:09:03 GMT-0500 (EST)",
           "date": "October 20, 2017"
        },
        {
           "_id": "5a130c70c9b53d2d73e10954",
           "text": "I bet. This homework as pretty much taken over my life. Im just trying to stay a float lol", (***ENCRYPT ***)
           "sender": "WiseN",
           "time": " 12:10:08 GMT-0500 (EST)",
           "date": "October 20, 2017"
        }
     ]
  }
  
]
}
    *
    * I am only adding the first message of the sample object above to the example below
    * */


	
   public static void main(String[]args)
   {
   	
   	//creating the object in the array for the key "messages"
      JSONObject msgObj = new JSONObject();
      msgObj.put("_id ", "5a130b5ac9b53d2d73e10952");
      msgObj.put("text", "Hey Whats Up");
      msgObj.put("sender", "WiseN");
      msgObj.put("time", " 12:05:29 GMT-0500 (EST)");
      msgObj.put("date", "October 20, 2017");
        //////////////////////////////////////////////////////////
      JSONObject msgObj2 = new JSONObject();
      msgObj2.put("_id ", "5a130b5ac9b53d2d73e10952");
      msgObj2.put("text", "Hey, Nothing much, this API has really been kicking my ass");
      msgObj2.put("sender", "WiseN");
      msgObj2.put("time", " 12:05:29 GMT-0500 (EST)");
      msgObj2.put("date", "October 20, 2017");
        


      //creating the array for the key "messages"
      JSONArray msgArray = new JSONArray();
      msgArray.put(msgObj);
      msgArray.put(msgObj2);
        
   	//Create an privateConvo object to put in the privateConvo(s) array
      JSONObject privateConvoObj = new JSONObject();
      privateConvoObj.put("recipientId", "TaslimD");
      privateConvoObj.put("_id", "WiseN");
      privateConvoObj.put("__v", 0);
      privateConvoObj.put("messages", msgArray); 
   
     // create a json array convArray
      JSONArray convoArray = new JSONArray();
        
   
      convoArray.put(privateConvoObj);

       JSONObject parentObj = new JSONObject();
       parentObj.put("_id", "WiseN");
       parentObj.put("_v", 4);
       parentObj.put("privateConvos",convoArray);
   
   //		------------------- ------------------- -------------------production code below this line ------------------- ------------------- ------------------- -------------------
   //		------------------- ------------------- -------------------production code below this line ------------------- ------------------- ------------------- -------------------
   

   
//      Encryption3 op1 = new Encryption3();
   
//      JSONObject returnedObject = op1.wrapper(parentObj, "encrypt", null);
//      System.out.println("returned Object AFTER ENCODE: "+returnedObject.toString(4));
   
   	//get the Arraylist from string key by mapper function
//      ArrayList<Integer> decryptKey =  op1.mapStringToAry(returnedObject.getString("sec"));
//      System.out.println("decrKey from MAIN: "+decryptKey.toString());
//       ArrayList<Integer> decryptKey = mapStringToAry(parentObj.getString("sec")); break;
//      returnedObject = op1.wrapper(parentObj, "decrypt", decryptKey);
//      System.out.println("returned Object AFTER DECODE: "+returnedObject.toString(4));
   }





	/*
	 * This function ranz generates random integers based on the length of a text
	 */
   private int[]ranz(String mystr){
      int [] randNum = new int[mystr.length()];
      for (int i = 0; i < randNum.length; i++){
         randNum[i] = (int)(Math.random()*26+1);
      }
      return randNum;
   }

	/*
	 * This function encrypt encrypts any given message.
	 */


   private String encrypt(String plainText, int[]shiftNum){
      String cipherText = "";
      int length = plainText.length();
   
      for(int i = 0; i<length; i++){
         int shift = shiftNum[i];
         char ch = plainText.charAt(i);
         if(Character.isLetter(ch)){
            if(Character.isLowerCase(ch)){
               char c = (char)(ch+shift);
               if(c>'z'){
                  cipherText += (char)(ch - (26-shift));
               }
               else{
                  cipherText += c;
               }
            }
            else if(Character.isUpperCase(ch)){
               char c = (char)(ch+shift);
               if(c>'Z'){
                  cipherText += (char)(ch - (26-shift));
               }
               else{
                  cipherText += c;
               }
            }
         }
         else{
            cipherText += ch;
         }
      }
      return cipherText;
   }
	/*
	 * This function decrypt decrypts any encrypted message.
	 */
   private String decrypt(String plainText, int[]shiftNum){
      String cipherText = "";
      int length = plainText.length();
      for(int i = 0; i<length; i++){
         int shift = shiftNum[i];
         char ch = plainText.charAt(i);
         if(Character.isLetter(ch)){
            if(Character.isLowerCase(ch)){
               char c = (char)(ch-shift);
               if(c<'a'){
                  cipherText += (char)(ch + (26-shift));
               }
               else{
                  cipherText += c;
               }
            }
            else if(Character.isUpperCase(ch)){
               char c = (char)(ch-shift);
               if(c<'A'){
                  cipherText += (char)(ch + (26-shift));
               }
               else{
                  cipherText += c;
               }
            }
         }
         else{
            cipherText += ch;
         }
      }
      return cipherText;
   }



        
   public JSONObject wrapper(JSONObject parentObj, String doWhat, String decryptKey)
   {
       ArrayList<Integer> randonNumsAryFromClient;

       //decipher if this is an encrypt or decryption call
       switch(doWhat)
       {
           case "encrypt":
               randonNumsAryFromClient = null; break;

           case "decrypt":
               //if no decrypt key, return null ...error has been handled in node server
               if(decryptKey == "")
               {
                   return null;
               }
               randonNumsAryFromClient = mapStringToAry(decryptKey); break;

               default:
                   System.out.println("FATAL ERROR FROM ENCRYPT / DECRYPT SWITCH!!!");

                   return null;
       }

          //create arrayList for randomNumber cipher ary for encrypting / decrypting
	   	  ArrayList<Integer> randomNumsAry = new ArrayList<Integer>();

      //get length of privateConvos
      int lengthOfPrivateConvos = parentObj.getJSONArray("privateConvos").length();
    	   
    	   //iterate over every convo in the array of private convos
      for(int i=0;i<lengthOfPrivateConvos;i++)
      {
      	   //get recipientId from parent Object (param)
         String recipientId = parentObj.getJSONArray("privateConvos").getJSONObject(i).getString("recipientId");
      
      	   //if we want to encrypt and we dont have a random numbers array from the client, we will encrypt
         if( doWhat.equals("encrypt") && randonNumsAryFromClient == null)
         {

         	   //get random number array for recipientId
            int[] randomNumAryForRecipientId = ranz(recipientId);
         
         	   //iterate over the array of random numbers for recipientId, and add each elm to randomNumsAry
            for(int e=0;e<randomNumAryForRecipientId.length;e++)
            {

               //adding random numbers from the random numbers array for recipientId to randomNumsAry
               randomNumsAry.add(randomNumAryForRecipientId[e]);
            
            }

         	System.out.println("randomNumsArray: "+randomNumsAry.toString()+" recipientId RandomNumsAry: "+Arrays.toString(randomNumAryForRecipientId));


            //get random number array for recipientId
//            int[] randomNumAryForRecipientId = ranz(recipientId);

            //encrypt the recipientId
            String encryptedRecipientId = encrypt(recipientId, randomNumAryForRecipientId);
         
            //System.out.println("encrypted RecipientId: "+encryptedRecipientId);
         
         	//set the encrypted RecipientId to the new value of the recipientId key in the parentObj
            parentObj.getJSONArray("privateConvos").getJSONObject(i).put("recipientId", encryptedRecipientId);
         
            //System.out.println("printed object afer recipientId encryption: "+parentObj.toString(3));
         
         
         } //if we want to decrypt and we have a random numbers array from the client, we will decrypt
         else if(doWhat.equals("decrypt") && randonNumsAryFromClient != null)
         {


            //get random numbers from randomNumsAry for recipientId & store in int variable
            int recipientIdLength = recipientId.length();

         	//pass length of returned array, and the client array to generate a short array of nums representing the recipientId
            int[] decryptAryForRecipientId = arrayGen(recipientIdLength, randonNumsAryFromClient);

            System.out.println("decrypt key for recipientId: "+Arrays.toString(decryptAryForRecipientId));

         	//decrypt the recipientId
            String decryptedRecipientId = decrypt(recipientId, decryptAryForRecipientId);

            System.out.println("decrypted recipientId: "+decryptedRecipientId);

         	//set the decrypted RecipientId to the new value of the recipientId key in the parentObj
            parentObj.getJSONArray("privateConvos").getJSONObject(i).put("recipientId", decryptedRecipientId);
         
           //System.out.println("printed object afer recipientId decryption: "+parentObj.toString(3));
         }
      	   
      	   
      	 //get the array of messages from the parent Object (param)
         JSONArray msgsAry = parentObj.getJSONArray("privateConvos").getJSONObject(i).getJSONArray("messages");
      	   
      	 //iterate over messages array of message JSON Objects
         for(int k=0;k<msgsAry.length();k++)
         {

         	//get one message object, and access text in JSON object
            String oneMsg = msgsAry.getJSONObject(k).getString("text");


         	//if we want to encrypt and we dont have a random numbers array from the client, we will encrypt
            if( doWhat.equals("encrypt") && randonNumsAryFromClient == null)
            {
            
               //generate random array of numbers for one message
               int[] randomNumAryForOneMsg = ranz(oneMsg);

               System.out.println("randomNumAryForOneMsg: "+randomNumAryForOneMsg.toString());
            
               //iterate over the array of random numbers for one message, and add each elm to randomNumsAry
               for (int e = 0; e < randomNumAryForOneMsg.length; e++) {
               
               	   //adding random numbers from the random numbers array for one message to randomNumsAry (for all msgs)
                  randomNumsAry.add(randomNumAryForOneMsg[e]);
               }
            
               System.out.println("randomNumsArray: " + randomNumsAry.toString() + " random nums array for one message: " + Arrays.toString(randomNumAryForOneMsg));

               //send random array of numbers for message, and text from message to encrypt function, store encrypted text
               String encryptedOneMsgText = encrypt(oneMsg, randomNumAryForOneMsg);

               System.out.println("encrypted OneMsgText: "+encryptedOneMsgText);

               //print the encrypted one message text
               System.out.println("encrypted OneMsg: "+encryptedOneMsgText);

               //set the encrypted one message object's text key's value in parentObj to the new value of the encryptedOneMsgText
               parentObj.getJSONArray("privateConvos").getJSONObject(i).getJSONArray("messages").getJSONObject(k).put("text", encryptedOneMsgText);
            
               System.out.println("printed object afer OneMesgText encryption: "+parentObj.toString(3));

            }
            else if(doWhat.equals("decrypt") && randonNumsAryFromClient != null)
            {
				//get random numbers from randomNumsAry for one message & store in int variable
               int oneMsgLength = oneMsg.length();

               //pass length of returned array, and the client array to generate a short array of nums representing the one message
               int[] decryptAryForOneMsgText = arrayGen(oneMsgLength, randonNumsAryFromClient);
            
               //decrypt the one message
               String decryptedOneMsgText = decrypt(oneMsg, decryptAryForOneMsgText);
            
               //print the decrypted one message text
               System.out.println("decrypted OneMsgText: "+decryptedOneMsgText);
            
               //set the decrypted one message object's text key's value in parentObj to the new value of the decryptedOneMsgText
               parentObj.getJSONArray("privateConvos").getJSONObject(i).getJSONArray("messages").getJSONObject(k).put("text", decryptedOneMsgText);

            }
         }
      
      }


      //create return wrapper object store the parentObject and the randomNumsAry in the JSON Object, and return it.
      JSONObject returnObj = new JSONObject();

      //put parent object in wrapper json object
      returnObj.put("data", parentObj);

      //add decryption key to array if we are encrypting
      if(doWhat.equals("encrypt"))
      {

          returnObj.put("sec", mapAryToString(randomNumsAry));
      }


   
   
      return returnObj;
   }

   private int[] arrayGen(int strLength, ArrayList<Integer> clientAry)
   {
      		//create decryption array of one encrypted string length
      int[] oneStrDecryptAry = new int[strLength];
   
      		//map clientAry elements to decryption array
      for(int i=0;i<strLength;i++)
      {
      		//remove and return the Integer removed from the client's randomNumsAryFromClient & store in decrypAry for one string
         oneStrDecryptAry[i] = clientAry.remove(0);
      
      }
   
      return oneStrDecryptAry;
   }


	   //when mapping array to string we want to spin each elm forward by the reverse elm at the end of the array & convert it to a string
   private String mapAryToString(ArrayList<Integer> ary)
   {
   	   //we will spin up the number by these values for each loop
      int spinUpValue1 = 179;
      int spinUpValue2 = 208;
   
   
      String resultString = "";
      int midPoint = (ary.size() / 2);
      int aryLength = ary.size();
      System.out.println("midpoint num: "+midPoint);
   
   
   	   //reverse "last with first" elements in the first half of the array and append to a string
      for (int i=midPoint; i >= 0; i-- )
      {
      
         int spinnedNum = (ary.get(i) + spinUpValue1);
      
      //				resultString += spinnedNum+",";
      
         resultString += Character.toString((char)spinnedNum);
      }
   
   	   //reverse "last with first" elements in the second half of the array and append to a string
      for(int k=aryLength-1; midPoint < k; k--)
      {
         int spinnedNum = (ary.get(k) + spinUpValue2);
      
      //		   		resultString += spinnedNum+", ";
         resultString += Character.toString((char)spinnedNum);
      
      
      }
      System.out.println("array: "+ary.toString());
      System.out.println("array length: "+ary.size());
      System.out.println("midpoint in array: "+ary.get(ary.size()/2).toString());
      System.out.println("final num in array: "+ary.get(ary.size()-1));
   
      System.out.println("----------------\nStr: "+resultString);
      System.out.println("lengthStr: "+resultString.length());
      System.out.println("midPoint num in string: "+resultString.charAt(resultString.length()/2));
      System.out.println("final num in string: "+resultString.charAt(resultString.length()-1));
   
      mapStringToAry(resultString);
   
      return resultString;
   }


   private ArrayList<Integer> mapStringToAry(String str)
   {
   	   //we will spin up the number by these values for each loop
      int spinDownValue1 = 179;
      int spinDownValue2 = 208;
      int strLength = str.length();

      String resultString = "";

   
   
      		//create arrayList
      ArrayList<Integer> myAryList = new ArrayList<Integer>();
   

   

   
   	    //get midPoint of array
      int midPoint = (strLength / 2);
   
   
   
   	   //reverse "last with first" elements in the first half of the array and append to a string
      for (int i=midPoint; i >= 0; i--)
      {
      
         int spinnedNum = (((int)str.charAt(i)) - spinDownValue1);
      
         myAryList.add(spinnedNum);
      
      
      }
   
   	   //reverse "last with first" elements in the second half of the array and append to a string
      for(int k=strLength-1; midPoint < k; k--)
      {
         int spinnedNum = (((int)str.charAt(k))  - spinDownValue2);
      
         myAryList.add(spinnedNum);
      
      }
   
   
   
   
   
   
   
   
   
   
   //
   //		   //get string without trailing comma
   //		   String properStr = str.substring(0,strLength-1);
   //
   //		   //map string to array
   //		   String[] strAry = properStr.split(",");
   //		   System.out.println("print Array: "+Arrays.toString(strAry));
   //		   //get midPoint of array
   //		   int midPoint = (strAry.length / 2);
   //
   //
   //
   //		   //reverse "last with first" elements in the first half of the array and append to a string
   //		   for (int i=midPoint; i >= 0; i--)
   //		   {
   //
   //			   int spinnedNum = (Integer.valueOf(strAry[i]) - spinDownValue1);
   //
   //			   myAryList.add(spinnedNum);
   //
   //
   //		   }
   //
   //		   //reverse "last with first" elements in the second half of the array and append to a string
   //		   for(int k=strAry.length-1; midPoint < k; k--)
   //		   {
   //			   int spinnedNum = Integer.valueOf(strAry[k].trim()) - spinDownValue2;
   //
   //			   myAryList.add(spinnedNum);
   //
   //		   }
   //
   
   
   
      System.out.println("----------\nDecoded Str AryList"+myAryList.toString());
      System.out.println("array length: "+myAryList.size());
      System.out.println("midpoint in array: "+myAryList.get(myAryList.size()/2).toString());
      System.out.println("final num in array: "+myAryList.get(myAryList.size()-1));
   
      return myAryList;
   }

}
