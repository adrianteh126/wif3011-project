import React from "react";
import ReactWordcloud, { Word } from "react-wordcloud";

type Props = {
  words: Word[];
};

const options = {
  fontSizes: [20, 60]
};

const WordCloudComponent: React.FC<Props> = ({ words }) => {
  return (
    <div style={{ display: "flex", alignItems: "center", justifyContent: "center", width: "100%", height: "500px" }}>
      <ReactWordcloud
        words={words}
        options={options}
      />
    </div>
  );
};

export default WordCloudComponent;
