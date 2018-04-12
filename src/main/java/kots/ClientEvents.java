//package kots;
//
////socketIO Client
//
//import io.socket.client.Ack;
//import io.socket.client.IO;
//import io.socket.client.Socket;
//import mainhotelapp.RestaurantPOSView;
//import org.apache.xpath.operations.Bool;
//
//
//public class ClientEvents
//{
//
//    static Socket socket = null;
//     RestaurantPOSView restuarentView = null;
//
//     void connectToServer(String ipAddress, String username, String password)
//    {
//        try{
//            socket = IO.socket("");
//        }catch(Exception e)
//        {
//            System.out.println("ERROR ON CONNECTING: "+e.getMessage());
//    }
//
//
//
////        socket!!.on(Socket.EVENT_DISCONNECT, object : Emitter.Listener {
////
////            override fun call(vararg args: Any) {}
////
////        })
//
//
//        socket.connect();
//        socket.open();
//
////        socket!!.emit(Socket.EVENT_CONNECT, "Hello!")
//
//
//        socket.send("hey");
////       socket!!.emit(EventNames.requestClientSignOn, usernameTextField.text)
//
////        val d = AckCallback("")
//        socket.emit("foo", "woot", new Ack() {
//            @Override
//            public void call(Object... obj)
//            {
//
//                System.out.println("response: "+obj);
//
//                //cast value to string from server, hope for encrypted password
//                String encryptedPassword = (String)obj[0];
//
//
//                switch(encryptedPassword)
//                {
//                    case "no user" : {
//                        //if no user, show a message telling user that username doesnt exist, shutdown and disconnect from server
//
//                    }
//
//                    default:  {
//
//                        String[] result = encryptedPassword.split("OR");
//
//                        Boolean isMatch =  password  == new dataProcessing.Encryption3().decryptValue("decrypt", result[0],result[1]);
//
//                        if(isMatch)
//                        {
//                            int cycleCount = 1;
//                            double duration = 0.5;
//                            restuarentView.animateHide(restuarentView.getKotsLoginPanel(),cycleCount,duration,restuarentView.getYAnimatorDistance())
//                        }else{
//
//                            String response = "This password does not match our records";
//                            System.out.println(response);
//                        }
//                    }
//                }
//            }
//
//
//
//    });
//
//
//
//    }
//}
