
# Greedy News

Its a basic news app, which show daily trending news. Its have custom category, search & save for later feature. Its also have offline feature.


## API Reference

#### News by headlines

```https
  GET https://newsapi.org/v2/top-headlines
```

| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `apiKey` | `string` | **Required**. Your API key |
| `country` | `string` | **Required**. Country ISO code (2 digit) |
| `category` | `string` |  Category |
| `language` | `string` |  Language |
| `q` | `string` |  Search for Title & Body |
| `pageSize` | `string` |  Default 20, Max 100 |



## Libary Usage

Add them in build.gradle

```bash
//Network calls
    implementation 'com.google.code.gson:gson:2.8.6'
    implementation 'com.squareup.okhttp3:logging-interceptor:3.4.1'
    implementation 'com.squareup.retrofit2:retrofit:2.6.1'
    implementation 'com.squareup.retrofit2:converter-gson:2.6.1'
//Image
    implementation 'com.github.bumptech.glide:glide:4.14.1'
    annotationProcessor 'com.github.bumptech.glide:compiler:4.14.1'
```
    
## Demo


https://s4.gifyu.com/images/Screenrecorder-2022-10-06-10-12-14-386_AdobeExpress-1.gif
## Features

- Daily Trending News
- Category Wise News
- Search By Title
- Offline News
- Save Later


## Lessons Learned

While creating this project I face lots of challenges and I learn lots of new things.
API calls, UI Design, Search functionality, Offline data handling using SharedPreferences, Save Later feature.

