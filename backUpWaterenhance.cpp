#include <opencv2/opencv.hpp>
#include <opencv2/core/mat.hpp>
#include <opencv2/core/core.hpp>
#include "opencv2/imgproc/imgproc.hpp"
#include <math.h>
#include <stdio.h>
using namespace std;
using namespace cv;

void balance_white(cv::Mat mat) { 
  double discard_ratio = 0.05;
  int hists[3][256];
  memset(hists, 0, 3*256*sizeof(int));

  for (int y = 0; y < mat.rows; ++y) {
    uchar* ptr = mat.ptr<uchar>(y);
    for (int x = 0; x < mat.cols; ++x) {
      for (int j = 0; j < 3; ++j) {
        hists[j][ptr[x * 3 + j]] += 1;
      }
    }
  }

  // cumulative hist
  int total = mat.cols*mat.rows;
  int vmin[3], vmax[3];
  for (int i = 0; i < 3; ++i) {
    for (int j = 0; j < 255; ++j) {
      hists[i][j + 1] += hists[i][j];
    }
    vmin[i] = 0;
    vmax[i] = 255;
    while (hists[i][vmin[i]] < discard_ratio * total)
      vmin[i] += 1;
    while (hists[i][vmax[i]] > (1 - discard_ratio) * total)
      vmax[i] -= 1;
    if (vmax[i] < 255 - 1)
      vmax[i] += 1;
  }


  for (int y = 0; y < mat.rows; ++y) {
    uchar* ptr = mat.ptr<uchar>(y);
    for (int x = 0; x < mat.cols; ++x) {
      for (int j = 0; j < 3; ++j) {
        int val = ptr[x * 3 + j];
        if (val < vmin[j])
          val = vmin[j];
        if (val > vmax[j])
          val = vmax[j];
        ptr[x * 3 + j] = static_cast<uchar>((val - vmin[j]) * 255.0 / (vmax[j] - vmin[j]));
      }
    }
  }
}

int main(int argc, char **argv)
{
    Mat image = imread(argv[1], 1);
    int beforeHeight = image.rows; 
    int beforeWidth = image.cols; 
    int k; // whatever scale
    @TODO assign k

    namedWindow("before", WINDOW_AUTOSIZE);
    
    resize(image, image, Size(beforeWidth*k, beforeHeight*k)); 
    imshow("before", image);
    balance_white(image); 
    namedWindow("with white balance", WINDOW_AUTOSIZE);
    imshow("with white balance", image);

    waitKey(0);
    return 0;
}
