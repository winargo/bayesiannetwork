const express = require('express')
const app = express()
const mysql = require('mysql')
const bodyParser = require('body-parser')
const port = 3000

app.use(bodyParser.urlencoded({ extended: true }))
// parse application/json
app.use(bodyParser.json())

const pool = mysql.createPool({
    host     : "localhost",
    user     : "root",
    password : "",
    database : "bayesian",
    port     : 3306
});

app.get('/', (req, res) => res.send('Hello World!'))

app.post('/login',(req,res)=>{
  var username = req.body.username;
  var password = req.body.password;
  pool.getConnection(function(err, conn) {
        var sqldat = "select * from user where username = ? and password = ?"
         conn.query(sqldat,[username,password],(err,resdata)=>{
          if(err){
            log.info(err);
          }
          else{
            if(resdata.length==0){
              res.json({status:false,message:"Username or password doesnt match any record",resdata})
            }
            else{
              res.json({status:true,message:"login success",resdata})
            }

          }
        })
    }
)
})

app.post('/register',(req,res)=>{
  var username = req.body.username;
  var password = req.body.password;
  var email = req.body.email;
  var jk = req.body.jk;
  var nama = req.body.nama;
  var hp = req.body.hp;
  var limit = req.body.limit;
  pool.getConnection(function(err, conn) {
        var sqldat = "insert into user (email,jenis_kelamin,nama,no_telp,password,transaction_limit,username) values (?,?,?,?,?,?,?)"
         conn.query(sqldat,[email,jk,nama,hp,password,limit,username],(err,resdata)=>{
          if(err){
            log.info(err);
          }
          else{
            res.json({status:true,message:"register success",resdata})
          }
        })
    }
)
})

app.post('/register/card',(req,res)=>{
  var username = req.body.username;
  var password = req.body.password;
  var cvv = req.body.cvv;
  var month = req.body.month;
  var name = req.body.cardname;
  var number = req.body.number;
  var type = req.body.type;
  var year = req.body.year;

  pool.getConnection(function(err, conn) {
        var sqldat = "insert into credit_card (card_type,card_number,card_cvv,card_month,card_year,card_name,card_user,status,verified) values (?,?,?,?,?,?,?,?,?) where ? in(select username from user where password=?)"
        conn.query(sqldat,[type,number,cvv,month,year,name,username,"1","1",username,password],(err,resdata)=>{
          if(err){
            log.info(err);
          }
          else{
            res.json({status:true,message:"login success",resdata})
          }
        })
    }
)
})

app.get('/products',(req,res)=>{

  pool.getConnection(function(err, conn) {
        var sqldat = "select * from product"
        conn.query(sqldat,[],(err,resdata)=>{
          if(err){
            log.info(err);
          }
          else{
            res.json({status:true,message:"success products",data:resdata})
          }
        })
    }
)
})

app.get('/transaction',(req,res)=>{

  pool.getConnection(function(err, conn) {
        var sqldat = "select * from transaction"
        conn.query(sqldat,[],(err,resdata)=>{
          if(err){
            log.info(err);
          }
          else{
            res.json({status:true,message:"succes transactions",data:resdata})
          }
        })
    }
)
})

app.post('/payment',(req,res)=>{
  var trans_id = req.body.trans_id;
  pool.getConnection(function(err, conn) {
        var sqldat = "select * from transaction"
        conn.query(sqldat,[],(err,resdata)=>{
          if(err){
            log.info(err);
          }
          else{
            res.json({status:true,message:"succes transactions",data:resdata})
          }
        })
    }
)
})



app.listen(port, () => console.log(`listening on port ${port}!`))
