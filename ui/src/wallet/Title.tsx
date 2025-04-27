import Typography from '@mui/material/Typography';
import { ReactNode } from 'react';

interface TitleProps {
  children?: ReactNode;
}

export const Title = (props: TitleProps) => {
  return (
    <Typography component="h2" variant="h6" color="primary" gutterBottom sx={{display: 'inline'}}>
      {props.children}
    </Typography>
  );
}