import React from "react";
import ReactWordcloud, { Word } from "react-wordcloud";

type Props = {
  words: Word[];
};

// interface word {
//   text: string;
//   value: number;
// };

// const callbacks = {
//   getWordColor: (word: word) => word.value > 50 ? "blue" : "red",
//   onWordClick: console.log,
//   onWordMouseOver: console.log,
//   getWordTooltip: (word: word) => `${word.text} (${word.value}) [${word.value > 50 ? "good" : "bad"}]`
// };

const options = {
//   rotations: 2,
//   rotationAngles: [-90, 0],
  fontSizes: [20, 50]
};

// const size = [600, 400];

const WordCloudComponent: React.FC<Props> = ({ words }) => {
  return (
    <div style={{ display: "flex", alignItems: "center", justifyContent: "center", width: "100%", height: "500px" }}>
      <ReactWordcloud
        words={words}
        // callbacks={callbacks}
        options={options}
        // size={size} 
      />
    </div>
  );
};

export default WordCloudComponent;
