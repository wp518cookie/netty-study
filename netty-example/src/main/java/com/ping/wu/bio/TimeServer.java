package com.ping.wu.bio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author wuping
 * @date 2019-04-17
 * 可以使用线程池处理拿到的请求，但是本质上还是采用了同步阻塞模型，无法从根本上解决问题。
 * 对输入流进行读取操作的时候，会一直阻塞下去，直至三件事情：1、有数据可读 2、可用数据读取完毕 3、发生空指针或者IO异常
 * 写入时：也会阻塞，只是所有要发送的字节全部写入完毕或发生异常，但是当消息的接收方处理缓慢的时候，不能及时从TCP缓冲区读取数据，
 * 会导致发送方的window size不断减小，直到为0，双方处于keep-alive状态，消息发送方无法再写入消息，write操作被无限制阻塞，线程
 * 无法被释放，还是会炸
 */

public class TimeServer {
    public static void main(String[] args) throws IOException {
        ServerSocket ss = new ServerSocket(8080);
        while (true) {
            Socket socket = ss.accept();
            new Thread(() -> process(socket)).start();
        }
    }

    private static void process(Socket socket) {
        BufferedReader in = null;
        PrintWriter out = null;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            String currentTime = null;
            String body = null;
            while (true) {
                body = in.readLine();
                if (body == null) {
                    break;
                }
                System.out.println("The time server receive order : " + body);
                try {
                    TimeUnit.MILLISECONDS.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ? new Date().toString() : "BAD ORDER";
                out.println(currentTime);
            }
        } catch (Exception e) {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (out != null) {
                out.close();
                out = null;
            }
            if (socket != null) {
                try {
                    socket.close();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                socket = null;
            }
        }
    }
}
