//
//  main.cpp
//  Server
//
//  Created by HD on 16/7/2.
//  Copyright © 2016年 msq. All rights reserved.
//

#include <iostream>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>
#include <unistd.h>
#include <dirent.h>

#define PORT_NUM    "50001"
#define INT_LEN     30

int getFileCount(char *path);

int main(int argc, const char * argv[]) {
    // insert code here...
    
//    uint32_t seqNum;
//    char reqLenStr[INT_LEN];
//    char seqNumStr[INT_LEN];
    
    static int curr = 0;
    
    struct sockaddr_storage claddr;
    int lfd, cfd, optval;
    
    socklen_t addrlen;
    struct addrinfo hints;
    struct addrinfo *result, *rp;
    
#define ADDRSTRLEN (NI_MAXHOST + NI_MAXSERV + 10)
    char addrStr[ADDRSTRLEN];
    char host[NI_MAXHOST];
    char service[NI_MAXSERV];
    
//    if (argc > 1 && strcmp(argv[1], "--help") == 0)
//        ;
    
    memset(&hints, 0, sizeof(struct addrinfo));
    hints.ai_canonname = NULL;
    hints.ai_addr = NULL;
    hints.ai_next = NULL;
    hints.ai_socktype = SOCK_STREAM;
    hints.ai_family = AF_UNSPEC;
    hints.ai_flags = AI_PASSIVE | AI_NUMERICSERV;
    
    if (getaddrinfo(NULL, PORT_NUM, &hints, &result) == -1)
    {
        printf("getaddrinfo error\n");
        exit(1);
    }
    
    optval = 1;
    lfd = 1;
    for (rp = result; rp != NULL; rp = rp->ai_next)
    {
        lfd = socket(rp->ai_family, rp->ai_socktype, rp->ai_protocol);
        if (lfd == -1)
            continue;
        
        if (setsockopt(lfd, SOL_SOCKET, SO_REUSEADDR, &optval, sizeof(optval)) == -1)
        {
            printf("setsockopt error\n");
            exit(1);
        }
        
        if (bind(lfd, rp->ai_addr, rp->ai_addrlen) == 0)
            break;
        
        close(lfd);
    }
    
    if (rp == NULL)
    {
        printf("Could not bind socket to any address\n");
        exit(1);
    }
    
    if (listen(lfd, 5) == -1)
    {
        printf("listen error\n");
        exit(1);
    }
    
    freeaddrinfo(result);
    
    while (1)
    {
        addrlen = sizeof(struct sockaddr_storage);
        cfd = accept(lfd, (struct sockaddr *)&claddr, &addrlen);
        
        if (cfd == -1)
        {
            printf("accept err\n");
            continue;
        }
        
        if (getnameinfo((struct sockaddr *)&claddr, addrlen, host, NI_MAXHOST, service, NI_MAXSERV, 0) == 0)
        {
            snprintf(addrStr, ADDRSTRLEN, "(%s, %s)", host, service);
        }
        else
        {
            snprintf(addrStr, ADDRSTRLEN, "(?UNKNOWN?)");
        }
        printf("Connection from %s\n", addrStr);
        
        char path[] = "/Users/HD/Desktop/Server/file/";
        int total = getFileCount(path);
        
        const size_t sz = 256;
        size_t nNumRead = 0;
        char temp[sz];
        
        nNumRead = read(cfd, temp, sz);
        if (temp[0] == 'G')
        {
            // client request photo
            char filename[256];
            sprintf(filename, "%s%d.jpg", path, (curr % total));
            
            FILE *file = fopen(filename, "rb");
            if (file != NULL)
            {
                printf("client request.\n");
                
                while (!feof(file))
                {
                    nNumRead = fread(temp, 1, sz, file);
                    write(cfd, temp, nNumRead);
                }
                
                fclose(file);
                curr++;
                
                printf("response finish ...\n");
            }
        }
        else
        {
            // client upload photo
            char filename[256];
            sprintf(filename, "%s%d.jpg", path, total);
            
            FILE * fstream = fopen(filename, "wb");
            if (fstream != NULL)
            {
                printf("client upload pic...\n");
                fwrite(temp, 1, nNumRead, fstream);
                while (1)
                {
                    nNumRead = read(cfd, temp, sz);
                    if (nNumRead == 0)
                        break;
                    
                    fwrite(temp, 1, nNumRead, fstream);
                }
                
                fclose(fstream);
                printf("client upload pic finish..\n");
            }
        }
        
        close(cfd);
    }
    
    return 0;
}

int getFileCount(char *path)
{
    printf("路径为[%s]\n", path);
    
    struct dirent* ent = NULL;
    DIR *pDir;
    pDir=opendir(path);
    //d_reclen：16表示子目录或以.开头的隐藏文件，24表示普通文本文件,28为二进制文件，还有其他……
    
    int count = 0;
    
    while (NULL != (ent=readdir(pDir)))
    {
        size_t len = strlen(ent->d_name);
        size_t typeLen = 4;
        
        if (len >= 4)
        {
            char *type = (char *)malloc(typeLen * sizeof(char));
            memset(type, 0, typeLen);
            
            char *name = ent->d_name;
            name = name + (len - typeLen);
            
            strncat(type, name, typeLen);
//            printf("type = %s\n", type);
            
            char typeStr[] = ".jpg";
            if (strcmp(type, typeStr) == 0)
            {
                count++;
            }
            
            free(type);
        }
    }
    
    return count;
}





