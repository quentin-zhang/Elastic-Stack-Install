# 1

## HJ1 字符串最后一个单词的长度

计算字符串最后一个单词的长度，单词以空格隔开，字符串长度小于5000。（注：字符串末尾不以空格为结尾）
intput: hello nowcoder
output: 8

```java
import java.util.Scanner; 
public class Main{
    public static void main(String[] args){
        Scanner s = new Scanner(System.in);
        String line = s.nextLine();
        String[] words = line.split(" ");
        int last = words.length - 1;
    System.out.println(words[last].length());
   }
}
```

写出一个程序，接受一个由字母、数字和空格组成的字符串，和一个字符，然后输出输入字符串中该字符的出现次数。（不区分大小写字母）
数据范围： 1≤n≤1000 
intput: ABCabc
        A
output: 2

```java
import java.util.Scanner;
public class Main{
    public static void main(String[] args){
        Scanner sc = new Scanner(System.in);
        String sourceStr = sc.nextLine().toLowerCase();
        String singleChar = sc.nextLine().toLowerCase();
        int count = 0;
        for(int i=0;i<sourceStr.length();i++){
            String source = String.valueOf(sourceStr.charAt(i));
            if(source.equals(singleChar)){
                count++;
            }
        }
        System.out.println(count);
    }
}
```

