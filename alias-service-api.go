package main

import (
	"fmt"
	"net/http"
	"time"
)

func main() {
	http.HandleFunc("/", HelloServer)
	http.ListenAndServe(":7080", nil)
}

func HelloServer(w http.ResponseWriter, r *http.Request) {
      startingTime := time.Now().UTC()
      time.Sleep(500 * time.Millisecond)
      endingTime := time.Now().UTC()
      var duration_Seconds time.Duration = endingTime.Sub(startingTime)
      fmt.Printf("Time Elapsed before returning : [%d] \n", duration_Seconds)
      fmt.Fprintf(w, "%s-alias", r.URL.Path[1:])
}