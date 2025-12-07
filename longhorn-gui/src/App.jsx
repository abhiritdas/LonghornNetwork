// src/App.jsx
import React, { useState, useRef } from 'react';
import networkData from './data.json'; 
import ForceGraph2D from 'react-force-graph-2d'; 
import { Button, Card, CardContent, Typography, Grid, TextField, List, ListItem, ListItemText, Chip, MenuItem } from '@mui/material';
import { createTheme, ThemeProvider } from '@mui/material/styles';

const theme = createTheme({
  palette: {
    primary: { main: '#bf5700' },
    secondary: { main: '#333f48' },
  },
});

function App() {
  const [selectedCaseIndex, setSelectedCaseIndex] = useState(0);
  
  const data = (networkData && networkData[selectedCaseIndex]) 
    ? networkData[selectedCaseIndex] 
    : { nodes: [], links: [], logs: [] };

  const [searchTerm, setSearchTerm] = useState('');
  const [highlightNodes, setHighlightNodes] = useState(new Set());
  const graphRef = useRef();

  // Reset highlights when switching test cases
  if (selectedCaseIndex !== 0 && highlightNodes.size > 0) {
      setHighlightNodes(new Set());
      setSearchTerm('');
  }

  if (!networkData || networkData.length === 0) {
      return <div style={{ padding: 20 }}>Waiting for data... Run Main.java</div>;
  }

  const handleSearch = () => {
    const matches = new Set();
    if (!searchTerm) {
      setHighlightNodes(new Set());
      return;
    }
    data.nodes.forEach(node => {
      if (node.internships && node.internships.some(i => i.toLowerCase().includes(searchTerm.toLowerCase()))) {
        matches.add(node.id);
      }
    });
    setHighlightNodes(matches);
    if (matches.size > 0 && graphRef.current) {
        graphRef.current.zoomToFit(400, 50, node => matches.has(node.id));
    }
  };

  return (
    <ThemeProvider theme={theme}>
      <div style={{ padding: '20px', backgroundColor: '#f5f5f5', minHeight: '100vh' }}>
        
        <Grid container alignItems="center" spacing={3} style={{marginBottom: '20px'}}>
            <Grid item>
                <Typography variant="h3" style={{ color: '#bf5700', fontWeight: 'bold' }}>
                🤘 Longhorn Network
                </Typography>
            </Grid>
            <Grid item>
                <TextField
                    select
                    label="Select Test Case"
                    value={selectedCaseIndex}
                    onChange={(e) => setSelectedCaseIndex(e.target.value)}
                    variant="outlined"
                    size="small"
                    style={{ minWidth: '200px', backgroundColor: 'white' }}
                >
                    {networkData.map((testCase, index) => (
                        <MenuItem key={index} value={index}>
                            {testCase.caseName || `Test Case ${index + 1}`}
                        </MenuItem>
                    ))}
                </TextField>
            </Grid>
        </Grid>

        <Grid container spacing={3}>
          {/* LEFT COLUMN: Graph */}
          <Grid item xs={12} md={8}>
            <Card elevation={3}>
              <CardContent>
                <Typography variant="h5" gutterBottom>
                    Visualization: {data.caseName}
                </Typography>
                <div style={{ height: '600px', border: '1px solid #ddd', borderRadius: '4px' }}>
                  <ForceGraph2D
                    ref={graphRef}
                    graphData={data}
                    // REMOVED: nodeAutoColorBy="group" 
                    
                    nodeCanvasObject={(node, ctx, globalScale) => {
                      const label = node.id;
                      const isHighlighted = highlightNodes.has(node.id);
                      const fontSize = 14 / globalScale; 
                      ctx.font = `bold ${fontSize}px Sans-Serif`;
                      const textWidth = ctx.measureText(label).width;
                      const padding = 10 / globalScale;
                      const radius = Math.max((textWidth / 1.6) + padding, 10 / globalScale);

                      ctx.beginPath();
                      ctx.arc(node.x, node.y, radius, 0, 2 * Math.PI, false);
                      
                      // CHANGED: Use fixed color (#bf5700) instead of node.color
                      ctx.fillStyle = isHighlighted ? '#ff0000' : '#bf5700';
                      
                      ctx.fill();
                      ctx.strokeStyle = '#fff';
                      ctx.lineWidth = 1.5 / globalScale;
                      ctx.stroke();

                      ctx.textAlign = 'center';
                      ctx.textBaseline = 'middle';
                      ctx.fillStyle = 'white'; 
                      ctx.fillText(label, node.x, node.y);
                    }}
                    nodePointerAreaPaint={(node, color, ctx) => {
                        // This uses the engine's interaction color for hit detection, DO NOT CHANGE
                        const fontSize = 14; 
                        ctx.font = `bold ${fontSize}px Sans-Serif`;
                        const textWidth = ctx.measureText(node.id).width;
                        const radius = Math.max((textWidth / 1.6) + 10, 10); 
                        ctx.beginPath();
                        ctx.arc(node.x, node.y, radius, 0, 2 * Math.PI, false);
                        ctx.fillStyle = color;
                        ctx.fill();
                    }}
                    linkCanvasObject={(link, ctx, globalScale) => {
                        const start = link.source;
                        const end = link.target;
                        if (!start || !end || !start.hasOwnProperty('x')) return;
                        ctx.beginPath();
                        ctx.moveTo(start.x, start.y);
                        ctx.lineTo(end.x, end.y);
                        ctx.lineWidth = 1.5 / globalScale; 
                        ctx.strokeStyle = '#999';
                        ctx.stroke();
                        
                        const textPos = { x: start.x + (end.x - start.x) / 2, y: start.y + (end.y - start.y) / 2 };
                        const fontSize = 12 / globalScale;
                        ctx.font = `bold ${fontSize}px Sans-Serif`;
                        const text = String(link.value);
                        const textWidth = ctx.measureText(text).width;
                        ctx.fillStyle = 'rgba(255, 255, 255, 0.9)';
                        ctx.fillRect(textPos.x - textWidth / 2 - 2, textPos.y - fontSize / 2 - 2, textWidth + 4, fontSize + 4);
                        ctx.textAlign = 'center';
                        ctx.textBaseline = 'middle';
                        ctx.fillStyle = '#000';
                        ctx.fillText(text, textPos.x, textPos.y);
                    }}
                    backgroundColor="#ffffff"
                  />
                </div>
                <Typography variant="caption" style={{ marginTop: '10px', display: 'block' }}>
                  * All nodes are uniform color. Numbers on lines represent Connection Strength.
                </Typography>
              </CardContent>
            </Card>
          </Grid>

          {/* RIGHT COLUMN: Controls */}
          <Grid item xs={12} md={4}>
            {/* Search */}
            <Card elevation={3} style={{ marginBottom: '20px' }}>
              <CardContent>
                <Typography variant="h6">Referral Search</Typography>
                <div style={{ display: 'flex', gap: '10px', marginTop: '10px' }}>
                  <TextField 
                    label="Company" 
                    variant="outlined" size="small" fullWidth
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                  />
                  <Button variant="contained" color="primary" onClick={handleSearch}>Find</Button>
                </div>
              </CardContent>
            </Card>

            {/* Roommates */}
            <Card elevation={3} style={{ marginBottom: '20px', maxHeight: '300px', overflow: 'auto' }}>
              <CardContent>
                <Typography variant="h6">Roommates</Typography>
                <List dense>
                  {data.nodes.filter(n => n.roommate !== "None").map((student, index) => (
                    <ListItem key={index} divider>
                      <ListItemText primary={<span><b>{student.id}</b> 🤝 {student.roommate}</span>} />
                    </ListItem>
                  ))}
                  {data.nodes.every(n => n.roommate === "None") && <Typography>No assignments.</Typography>}
                </List>
              </CardContent>
            </Card>

            {/* Logs */}
            <Card elevation={3} style={{ maxHeight: '300px', overflow: 'auto' }}>
              <CardContent>
                <Typography variant="h6">Activity Log</Typography>
                <div style={{ backgroundColor: '#eee', padding: '10px', fontSize: '12px' }}>
                  {data.logs && data.logs.length > 0 ? (
                    data.logs.map((log, index) => <div key={index} style={{borderBottom:'1px solid #ccc'}}>{log}</div>)
                  ) : "No activity."}
                </div>
              </CardContent>
            </Card>
          </Grid>
        </Grid>
      </div>
    </ThemeProvider>
  );
}

export default App;