using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Runtime.InteropServices;
using System.Text;
using System.Threading.Tasks;

namespace SamplesCS.Directly
{
    /// <summary>
    /// 关键帧图片操作
    /// </summary>
    public class ReadKeyFrame : ISample
    {
        private string ffmpeg = @"E:\code\opencvsharp_samples\SamplesCS\bin\Debug\ffmpeg\bin\ffmpeg.exe";
        private string inputName = @"E:\video\b.mp4";
        private string ratio = "640*480";
        private string picName = @"e:\output\keyframe-%02d.jpeg";
        public void Run()
        {
            //Console.WriteLine(dir);
            //Console.ReadLine();
            ReadKeyFrames();
        }

        /// <summary>
        /// 读取关键帧
        /// </summary>
        private void ReadKeyFrames()
        {
            try
            {
                //判断系统类型
                //如果是windows，直接使用ffmpeg.exe
                //如果是linux，则使用安装的ffmpeg（需要提前安装）
                /*
                  Linux工具调用：ffmpeg -i 333.jpg -q:v 31 -frames:v 1 -y image.jpg
                  windows:  ffmpeg.exe -i 333.jpg -q:v 31 -frames:v 1 -y image.jpg

                      -i 333.jpg 是输入文件
                      -q:v 31 是质量，值区间是2-31
                      -frames:v 1 是提取帧必要参数
                      -y 是遇到同名文件则覆盖 
                      image.jpg 输出文件名
                      还可以加 -s 160*100 表示输出宽高比为160*100
                 */
                //string outputFilePath = "image.jpg";//输出文件
                string cmdPath = ffmpeg;//ffmpeg工具对象
                string cmdParams = @" -i " + inputName + " -vf select='eq(pict_type\\,I)' -vsync 2 -s 640*480 -f image2 "+ picName;//命令参数
                if (RuntimeInformation.IsOSPlatform(OSPlatform.Windows))
                {
                    string dir = System.IO.Directory.GetCurrentDirectory() + @"\ffmpeg\bin\";
                    cmdPath = dir + "ffmpeg.exe";//根据实际的ffmpeg.exe文件路径来
                }
                else if (RuntimeInformation.IsOSPlatform(OSPlatform.Linux))
                {
                    cmdPath = "ffmpeg";//安装ffmpeg工具
                }
                else
                {
                    throw new Exception("当前操作系统不支持！");
                }

                using (System.Diagnostics.Process ffmpegProcess = new System.Diagnostics.Process())
                {
                    StreamReader errorReader;  // StringWriter to hold output from ffmpeg  
                                               // execute the process without opening a shell  
                    ffmpegProcess.StartInfo.UseShellExecute = false;
                    //ffmpegProcess.StartInfo.ErrorDialog = false;  
                    ffmpegProcess.StartInfo.WindowStyle = System.Diagnostics.ProcessWindowStyle.Hidden;
                    // redirect StandardError so we can parse it  
                    ffmpegProcess.StartInfo.RedirectStandardError = true;
                    // set the file name of our process, including the full path  
                    // (as well as quotes, as if you were calling it from the command-line)  
                    ffmpegProcess.StartInfo.FileName = cmdPath;

                    // set the command-line arguments of our process, including full paths of any files  
                    // (as well as quotes, as if you were passing these arguments on the command-line)  
                    ffmpegProcess.StartInfo.Arguments = cmdParams;

                    ffmpegProcess.Start();// start the process  

                    // now that the process is started, we can redirect output to the StreamReader we defined  
                    errorReader = ffmpegProcess.StandardError;

                    ffmpegProcess.WaitForExit();// wait until ffmpeg comes back  

                    //string result = errorReader.ReadToEnd();
                    //Console.WriteLine(result);
                    //Console.ReadLine();
                }
            }
            catch (Exception ex)
            {
                throw new Exception("生成图出错！", ex);
            }
        }
    }
}
