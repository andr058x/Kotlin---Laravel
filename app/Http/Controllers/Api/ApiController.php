<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\User;
use App\Todo;
use Auth;
use Hash;
use DB;
use Illuminate\Http\Request;

class ApiController extends Controller
{

    public function userlogin(Request $request){
        $email=$request->email;
        $password=$request->password;
        $customerInfo = array("email" => $email, "password" => $password);
        if (Auth::attempt($customerInfo)) {
        $existuser=User::where('email',$email)->first();
        $responseData = array('success' => '1', 'data' => $existuser, 'message' => 'Data has been returned successfully!'); 
        }
        else
        {
        $responseData = array('success' => '0', 'data' => array(), 'message' => "Invalid email or password.");
        }
        return response()->json($responseData, 200);
    }


    public function userregistration(Request $request){
            $email=$request->email;
            $name=$request->name;
            $password=$request->password;
            //check email existance
            $existUser = User::where('email', $email)->get();

            if (count($existUser) > 0) {
                //response if email already exit
                $postData = array();
                $responseData = array('success' => '0', 'data' => $postData, 'message' => "Email address is already exist");
            } else {

                //insert data into customer
                $user_id = User::insertGetId([
                    'name' => $name,
                    'email' => $email,
                    'password' => Hash::make($password)
                ]);
                $userData = User::find($user_id);
                $responseData = array('success' => '1', 'data' => $userData, 'message' => "Sign Up successfully!");
            }
            return response()->json($responseData, 200);
    }

    public function todolist(Request $request){
        $todo = Todo::where('user_id',$request->user_id)->get();
        $responseData = array('success' => '1','data'=>$todo, 'message' => "Todo list");
        return response()->json($responseData, 200);
    }

    public function tododetail(Request $request){
        $todo = Todo::find($request->id);
        $responseData = array('success' => '1','data'=>$todo, 'message' => "Todo detail");
        return response()->json($responseData, 200);
    }

    public function addtodo(Request $request){
            //insert data into todo
            $todo = new Todo;
            $todo->user_id=$request->user_id;
            $todo->title=$request->title;
            $todo->description=$request->description;
            $todo->image=$request->image;
            $todo->video=$request->video;
            $todo->audio=$request->audio;
            if($todo->save()){
                $responseData = array('success' => '1', 'message' => "Todo Successfully Added");
            }else{
                $responseData = array('success' => '0', 'message' => "Something want wrong");
            }
           
        return response()->json($responseData, 200);
}


   public function updatetodo(Request $request){
            //insert data into todo
            $todo = Todo::find($request->id);
            $todo->title=$request->title;
            $todo->description=$request->description;
            $todo->image=$request->image;
            $todo->video=$request->video;
            $todo->audio=$request->audio;
            if($todo->save()){
                $responseData = array('success' => '1', 'message' => "Todo Successfully Added");
            }else{
                $responseData = array('success' => '0', 'message' => "Something want wrong");
            }
           
        return response()->json($responseData, 200);
}


public function tododelete(Request $request){
    if(Todo::destroy($request->id)){
        $responseData = array('success' => '1', 'message' => "Todo Deleted Successfully");
    }else{
        $responseData = array('success' => '0', 'message' => "Something want wrong");
    }
    return response()->json($responseData, 200);
}


public function todostatus(Request $request){
     $todo = Todo::find($request->id);
     $todo->status=$request->status;
    if($todo->save()){
        $responseData = array('success' => '1', 'message' => "Todo status updated successfully");
    }else{
        $responseData = array('success' => '0', 'message' => "Something want wrong");
    }
    return response()->json($responseData, 200);
}
    

}
