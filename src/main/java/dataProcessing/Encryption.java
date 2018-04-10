/*
		Changes:
		- - - - -
		The caller is going to call your function like this (data type are in params):

		HashMap<String , String> returnValue =	op1.wrapperFunc(String , HashMap<String, String>);

		1) Your wrapper function should be able to accept a string parameter, and a HashMap of type <String, String>
		2) Write a switch or if statement to handle the request of encrypt or decrypt
		3) You should return a value of type HashMap<String, String>
		4) ALL of the methods inside of your wrapper function should be private and none-accessible outside of the class

		//DO NOT FOCUS ON, WE WILL REVISIT
		5) Remember the purpose of your algorithm, to encrypt and decrypt data; which means you need to store the key somewhere
		6) When your wrapper function is called to encrypt data, you should encrypt the array of random numbers, keep the key, and
			destroy the array of random numbers. IT SHOULD NOT BE STORED IN YOUR PROGRAM
		7) After you have encrypted the Array of random numbers (used to encrypt the user's message), that key to decrypt the array of
			random numbers should be returned inside of the hashMap with the key "decrypt"
	*/


package dataProcessing;

public class Encryption
{


	{}


	//This function ranz generates random integers based on the length of a text
	private static int[]ranz(String mystr){
		int [] randNum = new int[mystr.length()];
		for (int i = 0; i < randNum.length; i++){
			randNum[i] = (int)(Math.random()*26+1);
		}
		return randNum;
	}
	
	/*
	 * This function encrypt encrypts any given message.
	 */
	private static String encrypt(String plainText, int[]shiftNum){
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
	private static String decrypt(String plainText, int[]shiftNum){
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



	public static void main(String[] args)
	{
		String text = "I am enjoying this project";
		int [] myNum = ranz(text);
		String cipher = encrypt(text,myNum);
		System.out.println("The original text: " + text);
		System.out.println("The encrypted text: " +cipher);
		String decrypted = decrypt(cipher,myNum);
		System.out.println("The decripted text: " +decrypted);
	}

}
