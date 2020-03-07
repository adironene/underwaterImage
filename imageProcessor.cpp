#include <opencv2/opencv.hpp>
#include <opencv2/core/mat.hpp>
#include <opencv2/core/core.hpp>
#include "opencv2/imgproc/imgproc.hpp"
#include "opencv2/highgui/highgui.hpp"

using namespace std;
using namespace cv;

void contrast(cv::Mat image)
{
    double alpha = 1.3; //TODO: memorization of alpha, beta, and ratio values.
    int beta = -65;
    for (int y = 0; y < image.rows; y++)
        for (int x = 0; x < image.cols; x++)
            for (int c = 0; c < image.channels(); c++)
                image.at<Vec3b>(y, x)[c] = saturate_cast<uchar>(alpha * image.at<Vec3b>(y, x)[c] + beta);
}
void balance_white(cv::Mat mat)
{ //WHITE BALANCE CODE
    double discard_ratio = 0.05;
    int hists[3][256];
    memset(hists, 0, 3 * 256 * sizeof(int));

    for (int y = 0; y < mat.rows; ++y)
    {
        uchar *ptr = mat.ptr<uchar>(y);
        for (int x = 0; x < mat.cols; ++x)
            for (int j = 0; j < 3; ++j)
                hists[j][ptr[x * 3 + j]] += 1;
    }

    // cumulative hist
    int total = mat.cols * mat.rows;
    int vmin[3], vmax[3];
    for (int i = 0; i < 3; ++i)
    {
        for (int j = 0; j < 255; ++j)
            hists[i][j + 1] += hists[i][j];
        vmin[i] = 0;
        vmax[i] = 255;
        while (hists[i][vmin[i]] < discard_ratio * total)
            vmin[i] += 1;
        while (hists[i][vmax[i]] > (1 - discard_ratio) * total)
            vmax[i] -= 1;
        if (vmax[i] < 255 - 1)
            vmax[i] += 1;
    }

    for (int y = 0; y < mat.rows; ++y)
    {
        uchar *ptr = mat.ptr<uchar>(y);
        for (int x = 0; x < mat.cols; ++x)
        {
            for (int j = 0; j < 3; ++j)
            {
                int val = ptr[x * 3 + j];
                if (val < vmin[j])val = vmin[j];
                if (val > vmax[j])val = vmax[j];
                ptr[x * 3 + j] = static_cast<uchar>((val - vmin[j]) * 255.0 / (vmax[j] - vmin[j]));
            }
        }
    }
}

int main(int argc, char **argv)
{
    Mat image = imread(argv[1], 1);
    // for (int i = 1; i <= 1356; i++)
    // {
    // cout<<i<<endl;
    // string s = "/Users/phoebetang/Downloads/gate14.png";
    // s += to_string(i);
    // s += ".png";
    // Mat image = imread(s, 1);
    int beforeHeight = image.rows;
    int beforeWidth = image.cols;
    // namedWindow("before", WINDOW_AUTOSIZE);
    resize(image, image, Size(512, 512));
    // imshow("after white balance", image);
    balance_white(image);
    // imshow("before deblurr", image);
    contrast(image);
    // cvtColor( image, image, COLOR_BGRA2GRAY );
    // imshow("before equalize", image);
    // equalizeHist( image, image );
    // cvtColor(image, image, COLOR_GRAY2BGR);
    // cout<<"yeah yeet"<<endl;
    resize(image, image, Size(beforeWidth, beforeHeight));
    // namedWindow("with deblurr", WINDOW_AUTOSIZE);
    // imshow("with deblurr", image);
    cv::imwrite(argv[1], image);
    // waitKey(0);
    // }
    return 0;
}
