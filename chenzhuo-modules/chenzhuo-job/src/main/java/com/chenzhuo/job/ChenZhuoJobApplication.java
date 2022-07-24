package com.chenzhuo.job;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.chenzhuo.common.security.annotation.EnableCustomConfig;
import com.chenzhuo.common.security.annotation.EnableRyFeignClients;
import com.chenzhuo.common.swagger.annotation.EnableCustomSwagger2;

/**
 * 定时任务
 * 
 * @author chenzhuo
 */
@EnableCustomConfig
@EnableCustomSwagger2   
@EnableRyFeignClients
@SpringBootApplication
public class ChenZhuoJobApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(ChenZhuoJobApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  定时任务模块启动成功   ლ(´ڡ`ლ)ﾞ  \n" +
                " .-------.       ____     __        \n" +
                " |  _ _   \\      \\   \\   /  /    \n" +
                " | ( ' )  |       \\  _. /  '       \n" +
                " |(_ o _) /        _( )_ .'         \n" +
                " | (_,_).' __  ___(_ o _)'          \n" +
                " |  |\\ \\  |  ||   |(_,_)'         \n" +
                " |  | \\ `'   /|   `-'  /           \n" +
                " |  |  \\    /  \\      /           \n" +
                " ''-'   `'-'    `-..-'              ");
    }
}
