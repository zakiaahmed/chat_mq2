package chat_group;

import com.mysql.cj.xdevapi.Client;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class Client2 extends Thread{
    private int user_id=-1;
    public String username;
    private  String  cmd_result=null;
    private String login=null;
    private String response;

    ///////////////////////
    // login process

   public boolean login(String name,String password) throws IOException {
      String cmd1="login "+name+" "+password+"\n";
        handleClient(cmd1);
       String response = this.cmd_result;
       System.out.println("Response : "+response);
       if(! this.cmd_result.trim().equalsIgnoreCase("Ok login")){

           return false;
       }

       // startMessageReader();

       return true;
   }

    private void handleClient(String line) throws IOException{

        String[] tokens=line.split(" ");
       // if(tokens==null || tokens.length <= 0){continue;}

        String cmd=tokens[0];


         if("login".equalsIgnoreCase(cmd))
         {
             this.handleLogin(tokens);

                      }
         else
             if("login".equalsIgnoreCase(cmd))
             {
                 String[] tokensMsg=line.split(" ",3);
                 this.handleRegister(tokensMsg);

             }
            else{
                 String msg="unknown "+ cmd+"\n";
                 write(msg);
             }

    }

    private boolean isAuthenticated(String username,String password){
        dbOperations dbOp=new dbOperations();
        try {

            List<Map<String, Object>> users=dbOp.auth(username,password);

            if(users.size() == 1)
            {
                Map<String, Object> user=users.get(0);
                this.user_id=(int) user.get("user_id");

                return true;
            }

        } catch (SQLException e) {
            // e.printStackTrace();
        }

        return false;
    }

    private void handleLogin(String[] tokens) throws IOException {

        String msg = null;
        if(tokens.length ==3){
            String login=tokens[1];
            String password=tokens[2];

            if(
                    this.isAuthenticated(login,password)
            )
            {
              this.cmd_result="Ok login\n";
                msg="Ok login\n";

                this.login=login;
                System.out.println("user logged in successfully: "+login);
                String onlineMsg="online "+login+"\n";

            }else{
                msg="error login\n";
            }
        }

    }


    ///////////////////////
    // Regestration process
    public boolean register(String name,String password) throws IOException {
        String cmd1="register "+name+" "+password+"\n";
        String[] tokensMsg=cmd1.split(" ",3);
        handleRegister(tokensMsg);
        String response = this.cmd_result;
        System.out.println("Response : "+response);
        if(! this.cmd_result.trim().equalsIgnoreCase("ok register")){
            return false;
        }

        return true;
    }
    private void handleRegister(String[] tokens) throws IOException {
        String msg;
        if( tokens.length != 3){return;}

        String username=tokens[1];
        String password=tokens[2];

        if(this.exists(username)){

           this.cmd_result="error username exists\n";

            return;
        }

        int user_id=this.createUser(username,password);

        if(user_id==-1){
            this.cmd_result="error register failure\n";
        }else{
            this.cmd_result="ok register\n";
        }

    }

    private int createUser(String username, String password) {
        dbOperations dbOp=new dbOperations();

        try {
            int user_id=dbOp.newAccount(username, password);
            return user_id;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private boolean exists(String username) {
        dbOperations dbOp=new dbOperations();
        try{
            List<Map<String, Object>> users = dbOp.getUsers();
            for(Map<String, Object> user: users){
                if(username.equalsIgnoreCase((String)user.get("username"))){
                    return true;
                }
            }
        } catch (SQLException e) {
            // e.printStackTrace();
        }

        return false;
    }




    private void write(String msg) {
        System.out.println(msg);
    }

    private void startMessageReader() {

        multi_client t=new multi_client(this);
        t.start();
    }
}


  class multi_client extends Thread {

    Client2 client;

    public multi_client(Client2 client) {
        this.client = client;
    }

    public void run() {
        System.out.println("");

    }

}