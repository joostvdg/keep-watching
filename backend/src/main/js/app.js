'use strict';

/*jshint esversion: 6 */


import React from 'react';
import ReactDOM from 'react-dom';
import PageHeader from 'react-bootstrap/lib/PageHeader';
import Grid from 'react-bootstrap/lib/Grid';
import {ShowMovieList} from './movies/movies.js';

const rest = require('rest');
const mime = require('rest/interceptor/mime');

function formatDate(date) {
    return date.toLocaleDateString();
}

function MainPageHeader(props) {
    return (
        <PageHeader>
            Keep-Watching Frontend
        </PageHeader>
    );
}

function App(props) {
    return (<MainPage name="Joost"/>);
}


function MainPage(props) {
    return (
        <Grid >
            <MainPageHeader name="Joost"/>
            <ShowMovieList />
        </Grid>
    );
}

// Render the APP itself
const app = <App name="Joost"/>;
ReactDOM.render(app, document.getElementById('react'));

// setInterval(app, 100);
