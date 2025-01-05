package com.example.testapp;

public class Users {
    String Userid, Profilepic, mail, pass, Username, linkedUrl, lastmsg;

    public Users(){}

    public Users(String UserId,String UserName,String maill,String passs,String profilepic,String linkedinurl) {
        this.Userid=UserId;
        this.Username=UserName;
        this.mail=maill;
        this.pass=passs;
        this.Profilepic=profilepic;
        this.linkedUrl=linkedinurl;
    }

        public String getUserid () {
            return Userid;
        }

        public void setUserid (String userid){
            Userid = userid;
        }

        public String getProfilepic () {
            return Profilepic;
        }

        public void setProfilepic (String profilepic){
            Profilepic = profilepic;
        }

        public String getMail () {
            return mail;
        }

        public void setMail (String mail){
            this.mail = mail;
        }

        public String getPass () {
            return pass;
        }

        public void setPass (String pass){
            this.pass = pass;
        }

        public String getUsername () {
            return Username;
        }

        public void setUsername (String username){
            Username = username;
        }

        public String LinkedinURL () {
            return linkedUrl;
        }

        public void LinkedinURL (String linkedinurl){ this.linkedUrl = linkedinurl;}

        public String getLastmsg () {
            return lastmsg;
        }

        public void setLastmsg (String lastmsg){
            this.lastmsg = lastmsg;
        }
    }
