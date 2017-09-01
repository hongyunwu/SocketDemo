#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>
#include <stddef.h>
#include <string.h>
#include <sys/types.h>
#include <errno.h>
#include <sys/un.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <sys/epoll.h>
#include <fcntl.h>

#define clog_info(...) do { \
printf("<%s %s> %s %s:%d INFO:",__DATE__, __TIME__,__FUNCTION__,__FILE__,__LINE__)&& \
printf(__VA_ARGS__)&& \
printf("\n"); \
}while(0)

#define UNIXSTR_PATH "foo.socket"
#define FILE_NAME    "test"

//保证cmsghdr和msg_control对齐
union {
	struct cmsghdr cm;
	char control[CMSG_SPACE(sizeof(int))];
} control_un;

int main(int argc, char *argv[])
{
	int clifd = -1;
	int ret = -1;
    int filefd = -1;
    char buf[100] = {0};
	struct sockaddr_un servaddr;
	struct msghdr msg;
	struct iovec iov[1];
	struct cmsghdr *pcmsg;

    // 1. 创建套接字
	clifd  = socket(AF_UNIX, SOCK_STREAM, 0) ;
	if   ( clifd  <  0 ) {
		clog_info ( "socket failed" ) ;
		return  -1 ;
	}

    // 2. 创建文件描述符
	filefd  =  open(FILE_NAME ,O_CREAT | O_RDWR, 0777);
	if( filefd  <  0 ) {
		clog_info("open test failed.");
		return -1;
	}

	bzero (&servaddr, sizeof(servaddr));
	servaddr.sun_family = AF_UNIX;
	strcpy ( servaddr.sun_path, UNIXSTR_PATH);

    // 3. 连接服务器端
	ret = connect(clifd, (struct sockaddr*)&servaddr, sizeof(servaddr));
	if(ret < 0) {
		clog_info ( "connect failed." ) ;
		return -1;
	}

	msg.msg_name = NULL;
	msg.msg_namelen = 0;
	iov[0].iov_base = buf;
	iov[0].iov_len = 100;
	msg.msg_iov = iov;
	msg.msg_iovlen = 1;
	//设置缓冲区和长度
	msg.msg_control = control_un.control;
	msg.msg_controllen = sizeof(control_un.control);
	//直接通过CMSG_FIRSTHDR取得附属数据
	pcmsg = CMSG_FIRSTHDR(&msg);
	pcmsg->cmsg_len = CMSG_LEN(sizeof(int));
	pcmsg->cmsg_level = SOL_SOCKET;
	pcmsg->cmsg_type = SCM_RIGHTS;       // 指明发送的是描述符
	*((int*)CMSG_DATA(pcmsg)) = filefd;  // 把文件描述符写入辅助数据

    // 4. 发送文件描述符
	ret = sendmsg(clifd, &msg, 0);
	clog_info ("ret = %d, filedescriptor = %d", ret, filefd);
	return 0 ;
}
