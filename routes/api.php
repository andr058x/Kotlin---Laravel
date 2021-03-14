<?php

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;

/*
|--------------------------------------------------------------------------
| API Routes
|--------------------------------------------------------------------------
|
| Here is where you can register API routes for your application. These
| routes are loaded by the RouteServiceProvider within a group which
| is assigned the "api" middleware group. Enjoy building your API!
|
*/

Route::middleware('auth:api')->get('/user', function (Request $request) {
    return $request->user();
});

Route::group(['namespace' => 'Api'], function () {
    //user login url
    Route::post('userlogin', 'ApiController@userlogin');
    
    //user registration url
    Route::post('userregistration', 'ApiController@userregistration');

    //user addtodo url
    Route::post('addtodo', 'ApiController@addtodo');

    //user todolist url
    Route::post('todolist', 'ApiController@todolist');

    //user todo delete url
    Route::post('tododelete', 'ApiController@tododelete');

    //user  tododetail url
    Route::post('tododetail', 'ApiController@tododetail');
    
    //user  todostatus url
    Route::post('todostatus', 'ApiController@todostatus');
    
    //user  todostatus url
    Route::post('updatetodo', 'ApiController@updatetodo');

});
