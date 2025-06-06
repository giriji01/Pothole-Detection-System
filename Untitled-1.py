import requests
import pandas as pd
import mysql.connector
from mysql.connector import Error
from nrclex import NRCLex
import nltk

nltk.download('punkt', quiet=True)
nltk.download('punkt_tab', quiet=True)

API_KEY = 'WOGCtKyJy9uzYLFz'
ALERT_ID = '500138431'
URL = f'https://api.awario.com/v1.0/alerts/{ALERT_ID}/mentions?access_token={API_KEY}'
PARAMS = {'limit': 85}

DB_HOST = "revati.kohlihosting.in"
DB_USER = "kohliga"
DB_PASSWORD = "Killer@12345"
DB_NAME = "kohliga_Sentiment_Analysis"

def fetch_mentions():
    response = requests.get(URL, params=PARAMS)
    mentions = response.json().get('alert_data', {}).get('mentions', [])
    
    data = []
    if mentions:
        for mention in mentions:
            source = mention.get('source', 'N/A')
            url = mention.get('url', 'N/A')
            snippet = mention.get('snippet', '').strip()
            if snippet:
                data.append({"source": source, "url": url, "snippet": snippet})
        print(f"✅ Fetched {len(data)} mentions")
        return pd.DataFrame(data)
    else:
        print("⚠️ No mentions found")
        return pd.DataFrame()
    
    def analyze_sentiment(text):
    nrc = NRCLex(text)
    return dict(nrc.top_emotions)  # Convert list of tuples to dictionary

def classify_sentiment(emotions):
    sadness = emotions.get('sadness', 0)
    joy = emotions.get('joy', 0)
    fear = emotions.get('fear', 0)
    if joy > max(sadness, fear):
        score = min(1.0, 0.6 + joy)
        label = "positive"
    elif sadness + fear > joy:
        score = max(0.0, 0.4 - (sadness + fear) * 0.2)
        label = "negative"
    else:
        score = 0.5
        label = "neutral"
    
    return {"label": label, "score": score}

def save_to_db(df):
    try:
        conn = mysql.connector.connect(
            host=DB_HOST, user=DB_USER, password=DB_PASSWORD, database=DB_NAME
        )
        cursor = conn.cursor()

        # Create table
        cursor.execute("""
            CREATE TABLE IF NOT EXISTS Sentiments1 (
                id INT AUTO_INCREMENT PRIMARY KEY,
                source VARCHAR(255) NOT NULL,
                url VARCHAR(255) NOT NULL,
                snippet TEXT NOT NULL,
                emotions TEXT,
                sentiment_label VARCHAR(50),
                sentiment_score FLOAT,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                UNIQUE KEY unique_mention (source, url)
            )
        """)

        # Insert data
        if not df.empty:
            for _, row in df.iterrows():
                cursor.execute("""
                    INSERT IGNORE INTO Sentiments1
                        (source, url, snippet, emotions, sentiment_label, sentiment_score)
                    VALUES (%s, %s, %s, %s, %s, %s)
                """, (
                    row['source'], row['url'], row['snippet'],
                    str(row['emotions']), row['sentiment_label'], row['sentiment_score']
                ))
            conn.commit()
            print(f"✅ Inserted {len(df)} rows into MySQL")

    except Error as e:
        print(f"❌ Database error: {e}")
    
    finally:
        if conn.is_connected():
            cursor.close()
            conn.close()
            print("✅ Database connection closed")

            df = fetch_mentions()

if not df.empty:
    # Analyze sentiments
    df['emotions'] = df['snippet'].apply(analyze_sentiment)
    df[['sentiment_label', 'sentiment_score']] = df['emotions'].apply(
        lambda x: pd.Series(classify_sentiment(x))
    )

    # Display results
    print("\nSample of analyzed data:")
    print(df[['snippet', 'sentiment_label', 'sentiment_score']].head())

    # Save to database
    save_to_db(df)

    # Calculate and print total sentiment score
    total_score = df['sentiment_score'].sum()
    print(f"\nTotal Sentiment Score: {total_score}")