//
//  main.cpp
//  Client
//
//  Created by HD on 16/7/2.
//  Copyright © 2016年 msq. All rights reserved.
//

#include <iostream>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>
#include <unistd.h>

#define INT_LEN     30
#define PORT_NUM    "50001"

int main(int argc, const char * argv[]) {
    // insert code here...
    
    int cfd = -1;
    
    struct addrinfo hints;
    struct addrinfo *result, *rp;
    
    memset(&hints, 0, sizeof(struct addrinfo));
    hints.ai_canonname = NULL;
    hints.ai_addr = NULL;
    hints.ai_next = NULL;
    hints.ai_family = AF_UNSPEC;
    hints.ai_socktype = SOCK_STREAM;
    hints.ai_flags = AI_PASSIVE | AI_NUMERICSERV;
    
    char ip[] = "127.0.0.1";
    if (getaddrinfo(ip, PORT_NUM, &hints, &result) == -1)
    {
        printf("getaddrinfo error\n");
        exit(1);
    }
    
    for (rp = result; rp != NULL; rp = rp->ai_next)
    {
        cfd = socket(rp->ai_family, rp->ai_socktype, rp->ai_protocol);
        if (cfd == -1)
            continue;
        
        if (connect(cfd, rp->ai_addr, rp->ai_addrlen) != -1)
            break;
        
        close(cfd);
    }
    
    if (rp == NULL)
    {
        printf("Could not connect socket to any address\n");
        exit(1);
    }
    
    freeaddrinfo(result);
    
    // upload file
    FILE *file = fopen("/Users/HD/Desktop/Client/Client/test.jpg", "rb");
    if (file != NULL)
    {
        size_t nNumRead = 0;
        const size_t sz = 256;
        char temp[sz];
        
        while (!feof(file))
        {
            nNumRead = fread(temp, 1, sz, file);
            write(cfd, temp, nNumRead);
        }
        
        fclose(file);
    }
    
    // download file
//    char get[] = "Get";
//    write(cfd, get, strlen(get));
//    
//    FILE * fstream = fopen("/Users/HD/Desktop/Client/Client/getpic.jpg", "wb");
//    if (fstream != NULL)
//    {
//        const size_t sz = 256;
//        size_t nNumRead = 0;
//        char temp[sz];
//        
//        printf("downloading ...\n");
//        while (1)
//        {
//            nNumRead = read(cfd, temp, sz);
//            if (nNumRead == 0)
//                break;
//            
//            fwrite(temp, 1, nNumRead, fstream);
//        }
//        
//        fclose(fstream);
//        printf("download pic finish..\n");
//    }
    
    return 0;
}
