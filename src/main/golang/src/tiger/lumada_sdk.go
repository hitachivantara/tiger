package main

import (
	"fmt"
	"github.com/hitachivantara/lumada"
)

func main() {
	fmt.Println("Welcome to Lumada")

	//asset := lumada.Asset{Id: "9d23824d-5ac1-48e9-8b97-cad607938a8f"}
	//fmt.Println(asset)
	
	loginReq := lumada.LoginRequest{Username:"admin", Password:"RedmondAdmin%5"}
	lumada.Login(loginReq, "10.0.2.15")
}
