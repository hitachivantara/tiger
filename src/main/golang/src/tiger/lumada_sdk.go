package main

import (
	"fmt"
	"os"

	lumada "github.com/hitachivantara/go-lumada"
)

var debug bool
var host, username, password string

func main() {
	fmt.Println("Welcome to Lumada")

	for i := 0; i < len(os.Args); i++ {
		if "--host" == os.Args[i] {
			i++
			host = os.Args[i]
		} else if "--username" == os.Args[i] {
			i++
			username = os.Args[i]
		} else if "--password" == os.Args[i] {
			i++
			password = os.Args[i]
		} else if "--debug" == os.Args[i] {
			debug = true
		}
	}

	if host == "" || username == "" || password == "" {
		fmt.Println("Dude, I need host, username and password")
		return
	}

	lumada.Debug = debug

	//asset := lumada.Asset{Id: "9d23824d-5ac1-48e9-8b97-cad607938a8f"}
	//fmt.Println(asset)

	loginReq := lumada.LoginRequest{Username: username, Password: password}
	loginResp, err := lumada.Login(loginReq, host)
	if err != nil {
		fmt.Println(err)
	}

	fmt.Printf("Access token: %v\n", loginResp.AccessToken)

}
